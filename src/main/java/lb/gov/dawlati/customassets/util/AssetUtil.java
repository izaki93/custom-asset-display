package lb.gov.dawlati.customassets.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLink;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetLinkLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleResource;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import lb.gov.dawlati.customassets.model.MainAsset;
import lb.gov.dawlati.customassets.model.RelatedAsset;

public class AssetUtil {

	private static Logger _logger = LoggerFactory.getLogger(AssetUtil.class);
	
	public static String getJson(List<RelatedAsset> relatedAssets) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(relatedAssets);
		return json;
	}

	public static List<RelatedAsset> getRelatedAssetsbyAssetEntryId(long assetEntryId, ThemeDisplay themeDisplay) {

		String title = "serviceTitle";
		String description = "serviceDescription";
		String languageId = themeDisplay.getLanguageId();
		List<RelatedAsset> relatedAssets = new ArrayList<RelatedAsset>();
		List<AssetLink> currentArticleRelatedLinks = AssetLinkLocalServiceUtil.getDirectLinks(assetEntryId);

		for (AssetLink related_asset_entry : currentArticleRelatedLinks) {
			try {
				AssetEntry currentAssetEntry = AssetEntryLocalServiceUtil.getEntry(related_asset_entry.getEntryId2());

				JournalArticle relatedJournalArticle = JournalArticleLocalServiceUtil
						.getLatestArticle(currentAssetEntry.getClassPK());
				_logger.info(
						"--> Related Web Content :- " + relatedJournalArticle.getTitle(themeDisplay.getLanguageId()));

				Document document = SAXReaderUtil.read(relatedJournalArticle.getContent());

				Node titleNode = document.selectSingleNode("/root/dynamic-element[@name='" + title + "']/dynamic-content[@language-id='" + languageId + "']");
				String titleValue = titleNode.getText();
				_logger.info("titleValue : " + titleValue);

				Node descriptionNode = document.selectSingleNode("/root/dynamic-element[@name='" + description + "']/dynamic-content[@language-id='" + languageId + "']");
				String descriptionValue = descriptionNode.getText();
				_logger.info("descriptionValue : " + descriptionValue);

				RelatedAsset relatedAsset = new RelatedAsset();
				relatedAsset.setRelatedAssetTitle(titleValue);
				relatedAsset.setRelatedAssetDescription(descriptionValue);
				relatedAssets.add(relatedAsset);

			} catch (PortalException e) {
				_logger.error(e.getMessage(), e);
			} catch (DocumentException e) {
				_logger.error(e.getMessage(), e);
			}

		}
		return relatedAssets;
	}
	
	public static List<MainAsset> getMainAssetsByTagName(String tagName, ThemeDisplay themeDisplay) {

		List<MainAsset> mainAssets = new ArrayList<MainAsset>();
		
		try {
			AssetTag assetTag = AssetTagLocalServiceUtil.getTag(themeDisplay.getScopeGroupId(), tagName);

			AssetEntryQuery assetEntryQuery = new AssetEntryQuery();
			long[] tagIds = { assetTag.getTagId() };
			assetEntryQuery.setAnyTagIds(tagIds);
			List<AssetEntry> assetEntryList = AssetEntryLocalServiceUtil.getEntries(assetEntryQuery);

			for (AssetEntry assetEntry : assetEntryList) {
				JournalArticleResource journalArticleResource = JournalArticleResourceLocalServiceUtil
						.getJournalArticleResource(assetEntry.getClassPK());
				JournalArticle journalArticle = JournalArticleLocalServiceUtil
						.getArticle(journalArticleResource.getGroupId(), journalArticleResource.getArticleId());

				MainAsset mainAsset = new MainAsset();
				mainAsset.setAssetId(assetEntry.getEntryId());
				mainAsset.setAssetName(journalArticle.getTitle(themeDisplay.getLocale()));
				mainAssets.add(mainAsset);

				_logger.info("\n----------------------------------------------------------\n");
				_logger.info("Journal Article Title: " + journalArticle.getTitle(themeDisplay.getLocale()));
				_logger.info("Asset Entry Id : " + assetEntry.getEntryId());
				_logger.info("\n----------------------------------------------------------\n");
			}

		} catch (PortalException e) {
			_logger.error(e.getMessage(), e);
		} catch (SystemException e) {
			_logger.error(e.getMessage(), e);
		}
		return mainAssets;
	}

}
