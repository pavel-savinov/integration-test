/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.campaign.manager.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.campaign.manager.service.CampaignLocalServiceUtil;
import com.liferay.campaign.manager.util.CampaignStatus;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * The extended model implementation for the Campaign service. Represents a row in the &quot;Campaign&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * Helper methods and all application logic should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the {@link com.liferay.campaign.manager.model.Campaign} interface.
 * </p>
 *
 * @author Brian Wing Shun Chan
 */
@ProviderType
public class CampaignImpl extends CampaignBaseImpl {

	public CampaignImpl() {
	}

	@Override
	public String getDescription() {
		return CampaignLocalServiceUtil.getCampaignDescription(
			getCampaignId(), LocaleUtil.getMostRelevantLocale());
	}

	@Override
	public String getDescription(Locale locale) {
		return CampaignLocalServiceUtil.getCampaignDescription(
			getCampaignId(), locale);
	}

	@Override
	public Map<Locale, String> getDescriptionMap() {
		if (_descriptionMap != null) {
			return _descriptionMap;
		}

		_descriptionMap = CampaignLocalServiceUtil.getDescriptionMap(
			getCampaignId());

		return _descriptionMap;
	}

	@Override
	public String getDescriptionMapAsXML() {
		return LocalizationUtil.updateLocalization(
			getDescriptionMap(), StringPool.BLANK, "Description",
			LocaleUtil.toLanguageId(LocaleUtil.getMostRelevantLocale()));
	}

	@Override
	public String getName() {
		return CampaignLocalServiceUtil.getCampaignName(
			getCampaignId(), LocaleUtil.getMostRelevantLocale());
	}

	@Override
	public String getName(Locale locale) {
		return CampaignLocalServiceUtil.getCampaignName(
			getCampaignId(), locale);
	}

	@Override
	public Map<Locale, String> getNameMap() {
		if (_nameMap != null) {
			return _nameMap;
		}

		_nameMap = CampaignLocalServiceUtil.getNameMap(getCampaignId());

		return _nameMap;
	}

	@Override
	public String getNameMapAsXML() {
		return LocalizationUtil.updateLocalization(
			getNameMap(), StringPool.BLANK, "Name",
			LocaleUtil.toLanguageId(LocaleUtil.getMostRelevantLocale()));
	}

	@Override
	public int getStatus() {
		Date now = new Date();

		CampaignStatus status = CampaignStatus.values()[super.getStatus()];

		CampaignStatus newStatus = null;

		if (status == CampaignStatus.SCHEDULED) {
			if (now.after(getStartDate()) && now.before(getEndDate())) {
				newStatus = CampaignStatus.STARTED;
			}
			else if (now.after(getEndDate())) {
				newStatus = CampaignStatus.FINISHED;
			}
		}
		else if ((status == CampaignStatus.STARTED) &&
				 now.after(getEndDate())) {

			newStatus = CampaignStatus.FINISHED;
		}

		if (newStatus != null) {
			try {
				CampaignLocalServiceUtil.updateCampaignStatus(
					getCampaignId(), newStatus);
			}
			catch (PortalException pe) {
				_log.error("Error updating campaign status", pe);
			}

			status = newStatus;
		}

		return status.ordinal();
	}

	@Override
	public String getStatusLabel() {
		CampaignStatus status = CampaignStatus.values()[getStatus()];

		return StringUtil.toLowerCase(status.name());
	}

	private static final Log _log = LogFactoryUtil.getLog(CampaignImpl.class);

	private Map<Locale, String> _descriptionMap;
	private Map<Locale, String> _nameMap;

}