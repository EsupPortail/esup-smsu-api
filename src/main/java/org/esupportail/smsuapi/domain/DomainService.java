/**
 * SMS-U - Copyright (c) 2009-2014 Universite Paris 1 Pantheon-Sorbonne
 */
package org.esupportail.smsuapi.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.esupportail.smsuapi.dao.DaoService;
import org.esupportail.smsuapi.dao.beans.Application;
import org.esupportail.smsuapi.dao.beans.Sms;
import org.esupportail.smsuapi.domain.beans.sms.SmsStatus;
import org.esupportail.smsuapi.exceptions.UnknownMessageIdException;
import org.esupportail.ws.remote.beans.MsgIdAndPhone;
import org.esupportail.ws.remote.beans.TrackInfos;
import javax.inject.Inject;



/**
 * The basic implementation of DomainService.
 * 
 * See /properties/domain/domain-example.xml
 */
public class DomainService {

	@Inject private DaoService daoService;

	/**
	 * A logger.
	 */
	private final Logger logger = Logger.getLogger(getClass());

	//////////////////////////////////////////////////////////////
	// WS SendTrack methods
    //////////////////////////////////////////////////////////////
 	public TrackInfos getTrackInfos(final Integer msgId, Application app) 
			throws UnknownMessageIdException {
        logger.info("WS SendTrack receives the client request with parameter msgId = " + msgId);
        
		TrackInfos infos = new TrackInfos();
		infos.setNbDestTotal(daoService.getNbDest(msgId, app));
        if (infos.getNbDestTotal() == 0) {
            throw new UnknownMessageIdException();
        }
		infos.setNbDestBlackList(daoService.getNbSmsWithState(msgId, app, blacklistStatuses()));
		infos.setNbSentSMS(daoService.getNbSentSMS(msgId, app));
		infos.setNbProgressSMS(daoService.getNbProgressSMS(msgId, app));
		infos.setNbErrorSMS(daoService.getNbErrorSMS(msgId, app, errorStatuses()));
		infos.setListNumErreur(sms2phones(daoService.getListNumErreur(msgId, app, errorStatuses())));
		
		logger.info("Response TrackInfos object, for the client of WS SendTrack : " + 
				     "TrackInfos.NbDestTotal : " + infos.getNbDestTotal().toString() + 
				     "TrackInfos.NbSentSMS : " + infos.getNbSentSMS().toString() + 
				     "TrackInfos.NbProgressSMS : " + infos.getNbProgressSMS().toString() + 
				     "TrackInfos.NbDestBlackList :" + infos.getNbDestBlackList().toString() + 
				     "TrackInfos.NbErrorSMS : " + infos.getNbErrorSMS().toString());
		
		Set<String> listnums = infos.getListNumErreur();
		for (String phone : listnums) {
			logger.info("TrackInfos.NumErreur : " + phone);
	    	}
	    
		return infos;
	}
   
    private List<String> errorStatuses() {
		List<String> list = new ArrayList<>();
		list.add(SmsStatus.ERROR.name());
		list.add(SmsStatus.ERROR_PRE_BL.name());
        list.add(SmsStatus.ERROR_POST_BL.name());
        return list;
    }

    private List<String> blacklistStatuses() {
        List<String> list = new ArrayList<>();
		list.add(SmsStatus.ERROR_PRE_BL.name());
		list.add(SmsStatus.ERROR_POST_BL.name());
        return list;
    }

    private Set<String> sms2phones(List<Sms> smslist) {
		Set<String> nums = new HashSet<>();
		for (Sms sms : smslist) nums.add(sms.getPhone());
		return nums;
	}

	public List<String> getStatus(List<MsgIdAndPhone> listMsgIdAndPhone, Application app) {
		{
			final StringBuilder sb = new StringBuilder(500);
			logger.info("Receive request for SmsuapiStatus.getStatus:");
			for (MsgIdAndPhone m : listMsgIdAndPhone) sb.append(" " + m);
			logger.info(sb.toString());
        }
        
		List<String> l = new ArrayList<>();

		for (MsgIdAndPhone m : listMsgIdAndPhone) {
			Sms sms = getSms(m.getMsgId(), m.getPhoneNumber(), app);
			l.add(sms == null ? null : sms.getStateAsEnum().toString());
		}
		return l;
	}

    private Sms getSms(Integer msgId, String phoneNumber, Application app) {
		List<Sms> l = daoService.getSms(app, msgId, phoneNumber);
		if (l.size() == 0) {
			return null;
		} else if (l.size() > 1) {
			logger.error("internal error: more than one sms corresponds to " + msgId + ":" + phoneNumber);
			return null;
		} else {
			return l.get(0);
		}
	}

}
