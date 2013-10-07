package com.processconfiguration.cmap.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
        
        String editorArea = layout.getEditorArea();

        IFolderLayout bottom = layout.createFolder(
				"bottom", IPageLayout.BOTTOM, (float)0.7, editorArea);

        bottom.addView(Application.Views_CMapView_ID);
         
        IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.6, editorArea);
        left.addView(Application.Views_QuestionView_ID);
        
        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.55, editorArea);
        right.addView(Application.Views_ModelView_ID);
 	}
}
