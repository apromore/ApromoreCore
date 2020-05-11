/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

/**
 *
 */
package org.apromore.mapper;

import java.util.List;
import java.util.Map;

import org.apromore.dao.model.Cluster;
import org.apromore.dao.model.ClusteringSummary;
import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusterSettingsType;
import org.apromore.model.ClusterSummaryType;
import org.apromore.model.ClusterType;
import org.apromore.model.ClusteringParameterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.model.ConstrainedProcessIdsType;
import org.apromore.model.FragmentData;
import org.apromore.model.PairDistanceType;
import org.apromore.model.PairDistancesType;
import org.apromore.model.ProcessAssociationsType;
import org.apromore.service.model.ClusterFilter;
import org.apromore.service.model.ClusterSettings;
import org.apromore.service.model.MemberFragment;
import org.apromore.service.model.ProcessAssociation;
import org.apromore.toolbox.clustering.algorithm.dbscan.FragmentPair;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ClusterMapper {

    public static ClusterSummaryType convertClusterInfoToClusterSummaryType(Cluster c) {
        ClusterSummaryType ct = new ClusterSummaryType();
        ct.setClusterLabel(Integer.toString(c.getSize()));
        ct.setClusterId(c.getId());
        ct.setClusterSize(c.getSize());
        ct.setMedoidId(c.getMedoidId());
        ct.setAvgFragmentSize(c.getAvgFragmentSize());
        ct.setStandardizationEffort(c.getStandardizingEffort());
        ct.setRefactoringGain(c.getRefactoringGain());
        ct.setBCR(c.getBCR());
        return ct;
    }

    public static ClusterType convertClusterToClusterType(org.apromore.service.model.Cluster c) {
        ClusterType ct = new ClusterType();
        ct.setClusterLabel(Integer.toString(c.getCluster().getSize()));
        ct.setClusterId(c.getCluster().getId().toString());
        ct.setClusterSize(c.getCluster().getSize());
        ct.setMedoidId(c.getCluster().getMedoidId());
        ct.setAvgFragmentSize(c.getCluster().getAvgFragmentSize());
        ct.setStandardizationEffort(c.getCluster().getStandardizingEffort());
        ct.setRefactoringGain(c.getCluster().getRefactoringGain());
        ct.setBCR(c.getCluster().getBCR());

        List<MemberFragment> fs = c.getFragments();
        for (MemberFragment f : fs) {
            FragmentData fd = new FragmentData();
            fd.setFragmentId(f.getFragmentId());
            fd.setFragmentSize(f.getFragmentSize());
            fd.setDistance(f.getDistance());

            List<ProcessAssociation> pas = f.getProcessAssociations();
            for (ProcessAssociation pa : pas) {
                ProcessAssociationsType patype = new ProcessAssociationsType();
                patype.setProcessId(pa.getProcessId());
                patype.setProcessName(pa.getProcessName());
                patype.setBranchName(pa.getProcessBranchName());
                patype.setProcessVersionId(pa.getProcessVersionId());
                patype.setProcessVersionNumber(pa.getProcessVersionNumber());
                fd.getProcessAssociations().add(patype);
            }

            ct.getFragments().add(fd);
        }

        return ct;
    }

    public static ClusterFilter convertClusterFilterTypeToClusterFilter(ClusterFilterType cftype) {
        ClusterFilter filter = new ClusterFilter();
        filter.setMinClusterSize(cftype.getMinClusterSize());
        filter.setMaxClusterSize(cftype.getMaxClusterSize());
        filter.setMinAverageFragmentSize(cftype.getMinAvgFragmentSize());
        filter.setMaxAverageFragmentSize(cftype.getMaxAvgFragmentSize());
        filter.setMinBCR(cftype.getMinBCR());
        filter.setMaxBCR(cftype.getMaxBCR());
        return filter;
    }

    /**
     * @param clusterSettingsType
     * @return
     */
    public static ClusterSettings convertClusterSettingsTypeToClusterSettings(ClusterSettingsType clusterSettingsType) {
        ClusterSettings clusterSettings = new ClusterSettings();
        clusterSettings.setAlgorithm(clusterSettingsType.getAlgorithm());
        List<ClusteringParameterType> params = clusterSettingsType.getClusteringParams();
        for (ClusteringParameterType param : params) {
            if ("maxdistance".equalsIgnoreCase(param.getParamName())) {
                double maxDistance = Double.parseDouble(param.getParmaValue());
                clusterSettings.setMaxNeighborGraphEditDistance(maxDistance);
            }
        }
        ConstrainedProcessIdsType cpidsType = clusterSettingsType.getConstrainedProcessIds();
        if (cpidsType != null) {
            List<Integer> constrainedProcessIds = cpidsType.getProcessId();
            if (constrainedProcessIds != null && !constrainedProcessIds.isEmpty()) {
                clusterSettings.setConstrainedProcessIds(constrainedProcessIds);
            }
        }

        return clusterSettings;
    }

    /**
     * @param s
     * @return
     */
    public static ClusteringSummaryType convertClusteringSummaryToClusteringSummaryType(ClusteringSummary s) {
        ClusteringSummaryType st = new ClusteringSummaryType();
        st.setNumClusters(s.getNumClusters());
        st.setMinClusterSize(s.getMinClusterSize());
        st.setMaxClusterSize(s.getMaxClusterSize());
        st.setMinAvgFragmentSize(s.getMinAvgFragmentSize());
        st.setMaxAvgFragmentSize(s.getMaxAvgFragmentSize());
        st.setMinBCR(s.getMinBCR());
        st.setMaxBCR(s.getMaxBCR());
        return st;
    }

    public static PairDistancesType convertPairDistancesToPairDistancesType(Map<FragmentPair, Double> pairDistances) {
        PairDistancesType pdt = new PairDistancesType();
        for (FragmentPair pair : pairDistances.keySet()) {
            PairDistanceType p = new PairDistanceType();
            p.setFragmentId1(pair.getFid1());
            p.setFragmentId2(pair.getFid2());
            p.setDistance(pairDistances.get(pair));
            pdt.getPairDistance().add(p);
        }
        return pdt;
    }
}
