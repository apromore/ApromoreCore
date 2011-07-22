
package test_commom;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.net.URL;

import javax.xml.namespace.QName;

import org.test_toolbox.manager.ManagerPortalPortType;
import org.test_toolbox.manager.ManagerPortalService;
import org.test_toolbox.manager.RequestToToolbox;
import org.test_toolbox.manager.model_manager.ProcessSummariesType;
import org.test_toolbox.manager.model_manager.ProcessSummaryType;
import org.test_toolbox.manager.model_manager.VersionSummaryType;

/**
 * This class was generated by Apache CXF 2.4.1
 * 2011-07-21T09:03:57.235+10:00
 * Generated source version: 2.4.1
 * 
 */
public final class Client {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/manager/service_portal", "ManagerPortalService");
	private static ManagerPortalService ss ;
	private static ManagerPortalPortType port ;

	private Client() {
	}

	public static void main(String args[]) throws java.lang.Exception {


		URL wsdlURL = null;
		if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
			String host = args[0];
			wsdlURL = new URL("http://" + host + "/Apromore-manager/services/ManagerPortal?wsdl");
		} else {
			throw new Exception("argument must be host:port");
		}

		ss = new ManagerPortalService(wsdlURL, SERVICE_NAME);
		port = ss.getManagerPortal();  


		System.out.println("Invoking searchForSimilarProcesses...on whole database...");
		
		RequestToToolbox request = new RequestToToolbox();
		ProcessSummariesType queries = request.readProcessSummaries(port);
		int processId ;
		String versionName ;
		String method = "Greedy";
		Boolean latestVersions = true;
		double modelthreshold = 0.5;
		double labelthreshold = 0.5;
		double contextthreshold = 0.75;
		double skipnweight = 1;
		double subnweight = 1; 
		double skipeweight = 1;
		int errors = 0, processVersions = 0 ;
		for (int q=0;q<queries.getProcessSummary().size();q++) {
			ProcessSummaryType pQuery = queries.getProcessSummary().get(q);
			processId = pQuery.getId();
			for (int vq=0;vq<pQuery.getVersionSummaries().size();vq++){
				processVersions++;
				versionName = pQuery.getVersionSummaries().get(vq).getName();
				System.out.println("searchForSimilarProcesses.result for processId " + processId + " version "
						+ versionName + " is ");
				try {
					ProcessSummariesType res = request.searchForSimilarProcesses(port,
							processId,versionName,method,latestVersions,
							modelthreshold,labelthreshold,contextthreshold,skipnweight,subnweight,skipeweight);
					for (int i=0; i<res.getProcessSummary().size();i++) {
						ProcessSummaryType process = res.getProcessSummary().get(i);
						System.out.println ("       Process: " + process.getName());
						for (int j=0;j<process.getVersionSummaries().size();j++) {
							VersionSummaryType version = process.getVersionSummaries().get(j);
							System.out.println ("---------------- version: " + version.getName() 
									+ " with score " + version.getScore());
						}
					}
				} catch (Exception e) {
					System.out.println ("Error: " + e.getMessage());
					errors++;
					if (errors > 10) {
						System.out.println ("Too many errors give up!");
						System.exit(0);
					}
				}
			}

			System.out.println ("Summary: " + processVersions + " analysed. " + errors + " generated errors.");
		}

		System.exit(0);
	}

}
