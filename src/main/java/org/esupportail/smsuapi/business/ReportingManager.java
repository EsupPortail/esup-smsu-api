package org.esupportail.smsuapi.business;



import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Statistic;
import org.esupportail.smsuapi.exceptions.UnknownIdentifierApplicationException;
import org.esupportail.smsuapi.exceptions.UnknownMonthIndexException;
import org.esupportail.ws.remote.beans.ReportingInfos;

/**
 * Business layer concerning smsu service.
 *
 */
public class ReportingManager {
	
	
	/**
	 * Janvier.
	 */
	private static final  int JANUARY = 1;
	
	/**
	 * December.
	 */
	private static final  int DECEMBER = 12;
	/**
	 * Log4j logger.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = new LoggerImpl(getClass());

	/**
	 * {@link DaoService}.
	 */
	private DaoService daoService;
	
	/**
	 *  {@link ClientManager}.
	 */
	private ClientManager clientManager;
	
		//////////////////////////////////////////////////////////////
	// Constructeur
	//////////////////////////////////////////////////////////////
	/**
	 * constructor.
	 */
	public ReportingManager() {
		super();
	}
	
	public ReportingInfos getStats(final int month, final int year) 
			throws UnknownMonthIndexException, UnknownIdentifierApplicationException {
		Application app = clientManager.getApplication();
		
		{
			if ((JANUARY <= month) && (month <= DECEMBER)) {
			Statistic statistic = daoService.getStatisticByApplicationAndMonthAndYear(app, month, year);
			if (statistic != null) {
				ReportingInfos reportingInfos = new ReportingInfos();
				reportingInfos.setNbSms(statistic.getNbSms());
				reportingInfos.setNbSmsInError(statistic.getNbSmsInError());
				reportingInfos.setAccountLabel(statistic.getId().getAcc().getLabel());
				reportingInfos.setMonth(month);
				reportingInfos.setYear(year);
				return reportingInfos;
				} else {
					return null;
				}
			} else {
				throw new UnknownMonthIndexException("Unknown month. " 
						+ "Please set parameter month between 1 and 12");
				
			}
		}
		//ReportingInfos reportingInfos = daoService.getStats(month, year);
	   //return reportingInfos;
		
	}
	
	
	
	
	///////////////////////////////////////
	//  Mutators
	//////////////////////////////////////
	
	/**
	 * @param daoService the daoService to set
	 */
	public void setDaoService(final DaoService daoService) {
		this.daoService = daoService;
	}

	/**
	 * @param clientManager
	 */
	public void setClientManager(final ClientManager clientManager) {
		this.clientManager = clientManager;
	}

	

}
