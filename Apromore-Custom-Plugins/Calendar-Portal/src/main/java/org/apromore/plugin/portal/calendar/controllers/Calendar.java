/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.plugin.portal.calendar.controllers;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apromore.calendar.exception.CalendarNotExistsException;
import org.apromore.calendar.model.CalendarModel;
import org.apromore.calendar.model.HolidayModel;
import org.apromore.calendar.model.WorkDayModel;
import org.apromore.calendar.service.CalendarService;
import org.apromore.commons.datetime.TimeUtils;
import org.apromore.plugin.portal.calendar.Constants;
import org.apromore.plugin.portal.calendar.TimeRange;
import org.apromore.plugin.portal.calendar.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListModels;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import lombok.Getter;
import lombok.Setter;


/**
 * Controller for handling calendar interface Corresponds to calendar.zul
 */
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class Calendar extends SelectorComposer<Window> {

  @WireVariable("calendarService")
  CalendarService calendarService;

  private CalendarModel calendarModel;
  
  private boolean calendarExists=false;

  /**
   * For searching time zone id
   */
  private Comparator zoneComparator = new Comparator() {
    @Override
    public int compare(Object o1, Object o2) {
      String input = (String) o1;
      Zone zone = (Zone) o2;
      return zone.getDisplayName().toLowerCase().contains(input.toLowerCase()) ? 0 : 1;
    }
  };

  private static Logger LOGGER = LoggerFactory.getLogger(Calendar.class);
  private static final OffsetTime DEFAULT_START_TIME =
      OffsetTime.of(LocalTime.of(9, 0), ZoneOffset.UTC);
  private static final OffsetTime DEFAULT_END_TIME =
      OffsetTime.of(LocalTime.of(17, 0), ZoneOffset.UTC);

  @Wire("#actionBridge")
  Div actionBridge;

  @Wire("#dayOfWeekListbox")
  Listbox dayOfWeekListbox;

  @Wire("#holidayListbox")
  Listbox holidayListbox;

  @Wire("#zoneCombobox")
  Combobox zoneCombobox;

  // FIXME:
  // Replace with models from Apromore Calendar
  @Getter
  @Setter
  private List<OffsetTime> dayOfWeekStartTimes;
  @Getter
  @Setter
  private List<OffsetTime> dayOfWeekEndTimes;
  @Getter
  @Setter
  private List<LocalDate> holidayDates;
  @Getter
  @Setter
  private List<String> holidayDescriptions;
  @Getter
  @Setter
  private List<String> holidayTypes;

  private ListModelList<WorkDayModel> dayOfWeekListModel;
  private ListModelList<HolidayModel> holidayListModel;
  private ListModelList<Zone> zoneModel;  
 

  public Calendar() throws Exception {}

  @Override
  public void doAfterCompose(Window win) throws Exception {
    super.doAfterCompose(win);

    Long calendarId = (Long) Executions.getCurrent().getArg().get("calendarId");
    calendarExists=calendarId!=null;
    calendarModel = !calendarExists ? new CalendarModel(): calendarService.getCalendar(calendarId);

    populateTimeZone();
    initialize();

    actionBridge.addEventListener("onLoaded", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        rebuild();
      }
    });

    actionBridge.addEventListener("onSyncRows", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        syncRows();
      }
    });

    actionBridge.addEventListener("onEditRange", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        JSONObject params = (JSONObject) event.getData();
        int dowIndex = (Integer) params.get("dow");
        int index = (Integer) params.get("index");
        int startHour = (Integer) params.get("startHour");
        int startMin = (Integer) params.get("startMin");
        int endHour = (Integer) params.get("endHour");
        int endMin = (Integer) params.get("endMin");
        Date start =
            TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, startHour, startMin);
        Date end = TimeUtils.localDateAndTimeToDate(Constants.LOCAL_DATE_REF, endHour, endMin);
        editWorkday(dowIndex, index, start, end);
      }
    });

    actionBridge.addEventListener("onUpdateWorkday", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {
        JSONObject params = (JSONObject) event.getData();
        int dowIndex = (Integer) params.get("dow");
        Boolean workday = (Boolean) params.get("workday");
        WorkDayModel dowItem = getDayOfWeekItem(dowIndex);
        dowItem.setWorkingDay(workday);
        refresh(dowItem);
        Clients.evalJavaScript("Ap.calendar.buildRow(" + dowIndex + ")");
      }
    });

    actionBridge.addEventListener("onUpdateRanges", new EventListener<Event>() {
      @Override
      public void onEvent(Event event) throws Exception {        
        JSONObject params = (JSONObject) event.getData();
        int dowIndex = (Integer) params.get("dow");
        JSONArray rangeArray = (JSONArray) params.get("ranges");
        for (Object range : rangeArray) {
          JSONObject item = (JSONObject) range;
          int startHour = (Integer) item.get("startHour");
          int startMin = (Integer) item.get("startMin");
          int endHour = (Integer) item.get("endHour");
          int endMin = (Integer) item.get("endMin");                    
          WorkDayModel dowItem = getDayOfWeekItem(dowIndex);
          // TO DO: WorkDayModel currently only support a single range
          dowItem.setStartTime(OffsetTime.from(OffsetTime.of(LocalTime.of(startHour, startMin), ZoneOffset.UTC)));
          dowItem.setEndTime(OffsetTime.from(OffsetTime.of(LocalTime.of(endHour, endMin), ZoneOffset.UTC)));          
          refresh(dowItem);
        }        
        Clients.evalJavaScript("Ap.calendar.buildRow(" + dowIndex + ")");
      }
    });
  }

  public void populateTimeZone() {
    zoneModel = new ListModelList<Zone>();
    Set<String> zoneIds = ZoneId.getAvailableZoneIds();

    Zone selectedZone=null;
    for (String id : zoneIds) {
      ZoneId zoneId = ZoneId.of(id);
      Zone currentZone = new Zone(zoneId.getId(), zoneId.getDisplayName(TextStyle.FULL, Locale.US));
      zoneModel.add(currentZone);
      if(zoneId.equals(ZoneId.of(calendarModel.getZoneId())) || 
    		  (selectedZone==null && zoneId.equals(ZoneId.systemDefault())))
      {
    	  selectedZone=currentZone;
      }
      
    }
    zoneModel.addToSelection(selectedZone);
    zoneModel.setMultiple(false);
    ListModel listSubModel = ListModels.toListSubModel(zoneModel, zoneComparator, zoneIds.size());
    zoneCombobox.setModel(listSubModel);
  }

  public WorkDayModel getDayOfWeekItem(int dowIndex) {    
    return (WorkDayModel) dayOfWeekListbox.getModel().getElementAt(dowIndex-1);
  }

  public void refresh(WorkDayModel dowItem) {	    
    int index = dayOfWeekListModel.indexOf(dowItem);
    dayOfWeekListModel.set(index, dowItem); // trigger change
  }

  public void editWorkday(int dowIndex, int index, Date start, Date end) {
    try {
      Map arg = new HashMap<>();
      arg.put("parentController", this);
      arg.put("dowIndex", dowIndex);
      arg.put("index", index);
      arg.put("start", start);
      arg.put("end", end);
      Window window = (Window) Executions.getCurrent()
          .createComponents("calendar/zul/edit-range.zul", getSelf(), arg);
      window.doModal();

    } catch (Exception e) {
      LOGGER.error("Unable to create add holidays dialog", e);
    }
  }

  @Listen("onUpdateHolidayDescription = #holidayListbox")
  public void onUpdateHolidayDescription(ForwardEvent event) throws Exception {
    InputEvent inputEvent = (InputEvent) event.getOrigin();
    Textbox textbox = (Textbox) inputEvent.getTarget();
    String description = (String) inputEvent.getValue();
    Listitem listItem = (Listitem) textbox.getParent().getParent();
    HolidayModel holiday = (HolidayModel) listItem.getValue();
    holiday.setDescription(description);
  }

  @Listen("onUpdateHolidayDate = #holidayListbox")
  public void onUpdateHolidayDate(ForwardEvent event) throws Exception {
    InputEvent inputEvent = (InputEvent) event.getOrigin();
    Datebox datebox = (Datebox) inputEvent.getTarget();
    Object val = (Object) inputEvent.getValue();
    Date date;
    if (val instanceof Date) {
      date = (Date) val;
    } else {
      date = new SimpleDateFormat("yyyy MMM dd").parse((String) val);
    }
    LocalDate holidayDate = TimeUtils.dateToLocalDate(date);
    Listitem listItem = (Listitem) datebox.getParent().getParent();
    HolidayModel holiday = (HolidayModel) listItem.getValue();
      holiday.setHolidayDate(holidayDate);
   
  }

  @Listen("onRemoveHoliday = #holidayListbox")
  public void onRemoveHoliday(final Event event) {
    try {
      HolidayModel holiday = (HolidayModel) event.getData();
      removeHoliday(holiday);
    } catch (Exception e) {
      LOGGER.error("Unable to create remove a holiday", e);
    }
  }

  @Listen("onClick = #importHolidaysBtn")
  public void onClickImportHolidaysBtn() {
    try {
      Map arg = new HashMap<>();
      arg.put("country", "Australia");
      arg.put("parentController", this);
      Window window = (Window) Executions.getCurrent()
          .createComponents("calendar/zul/import-holidays.zul", getSelf(), arg);
      window.doModal();

    } catch (Exception e) {
      LOGGER.error("Unable to create add holidays dialog", e);
    }
  }

  @Listen("onClick = #addHolidayBtn")
  public void onClickAddHolidayBtn() {
    try {
      Map arg = new HashMap<>();
      arg.put("parentController", this);
      Window window = (Window) Executions.getCurrent()
          .createComponents("calendar/zul/add-holiday.zul", getSelf(), arg);
      window.doModal();

    } catch (Exception e) {
      LOGGER.error("Unable to create add holidays dialog", e);
    }
  }

  public void updateRange(int dowIndex, int index, int startHour, int startMin, int endHour,
      int endMin) {
    String cmd = String.format("Ap.calendar.updateRange(%d, %d, %d, %d, %d, %d)", dowIndex, index,
        startHour, startMin, endHour, endMin);
    Clients.evalJavaScript(cmd);
  }

  public void deleteRange(int dowIndex, int index) {
    String cmd = String.format("Ap.calendar.deleteRange(%d, %d)", dowIndex, index);
    Clients.evalJavaScript(cmd);
  }

  public void addHoliday(HolidayModel holiday) {
    LocalDate holidayDate = holiday.getHolidayDate();   
      holidayListModel.add(holiday);
  }

  public void removeHoliday(HolidayModel holiday) {
    holidayListModel.remove(holiday);
  }

  public void addHolidays(List<HolidayModel> holidays) {
	  holidayListModel.addAll(holidays);
  }

  public void rebuild() {
    Clients.evalJavaScript("(function () { if (Ap.calendar && Ap.calendar.rebuild) { Ap.calendar.rebuild(); } })()");
  }

  public void rebuildRow(int dowIndex, String json) {
    Clients.evalJavaScript("(function () { if (Ap.calendar && Ap.calendar.updateRanges) { Ap.calendar.updateRanges(" +
            Integer.toString(dowIndex) + "," + json +
            "); } })()");
  }

  public void syncRows() {
    for (int i = 1; i < 8; i++) {
      String json = toJSON(i);
      rebuildRow(i, json);
    }
  }

  public void initialize() {
    dayOfWeekListModel = new ListModelList<WorkDayModel>();    
    holidayListModel = new ListModelList<HolidayModel>();

    rebuild();
    if (calendarModel != null) {
      fromModels();
    } else {
      mock();
    }
    // This is called subsequently

    dayOfWeekListbox.setModel(dayOfWeekListModel);
    holidayListbox.setModel(holidayListModel);
  }

  private void fromModels() {
    List<WorkDayModel> workDays = calendarModel.getOrderedWorkDay();
    List<HolidayModel> holidays = calendarModel.getHolidays();
    dayOfWeekStartTimes = new ArrayList<OffsetTime>();
    dayOfWeekEndTimes  = new ArrayList<OffsetTime>();
    holidayListModel.addAll(holidays);
    dayOfWeekListModel.addAll(workDays);
  }

  private String toJSON(int dowIndex) {
    String json = "[";
    DayOfWeek dow = DayOfWeek.of(dowIndex);
    WorkDayModel dowItem = (WorkDayModel) dayOfWeekListbox.getModel().getElementAt(dowIndex-1);    
    json += "{";
    
      json += "startHour: " + Integer.toString(dowItem.getStartTime().getHour()) + ",";
      json += "startMin: " + Integer.toString(dowItem.getStartTime().getMinute()) + ",";
      json += "endHour: " + Integer.toString(dowItem.getEndTime().getHour()) + ",";
      json += "endMin: " + Integer.toString(dowItem.getEndTime().getMinute());
    
    json += "}";
    json += "]";
    return json;
  }

  private void toModels() {


    if(calendarExists)
    {
    	try {
    		
    		
    		calendarService.updateZoneInfo(calendarModel.getId(), 
					((Zone)zoneCombobox.getModel().getElementAt(zoneCombobox.getSelectedIndex())).getId());
    		
    		calendarService.updateWorkDays(calendarModel.getId(), 
					(List<WorkDayModel>) dayOfWeekListbox.getModel());
    		
			calendarService.updateHoliday(calendarModel.getId(), 
					(List<HolidayModel>) holidayListbox.getModel());
		} catch (CalendarNotExistsException e) {
//			 post event to notificaton
		}
    }

  }

  private void mock() {
    for (int i = 1; i < 8; i++) {
      List<TimeRange> ranges = new ArrayList<>();
      ranges.add(
          new TimeRange(OffsetTime.from(DEFAULT_START_TIME), OffsetTime.from(DEFAULT_END_TIME)));
      DayOfWeek dow = DayOfWeek.of(i);
      WorkDayModel dowItem = new WorkDayModel();
      dowItem.setWorkingDay(true);      
      dowItem.setDayOfWeek(dow);
      dowItem.setStartTime(OffsetTime.from(DEFAULT_START_TIME));
      dowItem.setEndTime(OffsetTime.from(DEFAULT_END_TIME));
      dayOfWeekListModel.add(dowItem);      
    }
    // holidayListModel.add(new HolidayItem(LocalDate.parse("2020-12-25"), "Christmas"));
    // holidayListModel.add(new HolidayItem(LocalDate.parse("2020-01-01"), "New Year Day"));
  }

  @Listen("onClick = #applyBtn")
  public void onClickApplyBtn() {
    toModels();
    getSelf().detach();
  }

  @Listen("onClick = #cancelBtn")
  public void onClickCancelBtn() {
    getSelf().detach();
  }

}
