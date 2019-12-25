package lb.gov.dawlati.customassets.portlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import lb.gov.dawlati.customassets.constants.CustomAssetDisplayPortletKeys;
import lb.gov.dawlati.customassets.model.MainAsset;
import lb.gov.dawlati.customassets.model.RelatedAsset;
import lb.gov.dawlati.customassets.util.AssetUtil;
import lb.gov.dawlati.customassets.util.ResponseWriterUtil;

/**
 * @author Ibrahim
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-js=/js/main.js","com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=CustomAssetDisplay", "javax.portlet.init-param.template-path=/",
		"com.liferay.portlet.requires-namespaced-parameters=false",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CustomAssetDisplayPortletKeys.CUSTOMASSETDISPLAY,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class CustomAssetDisplayPortlet extends MVCPortlet {

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		
		HttpServletRequest request = PortalUtil.getHttpServletRequest(renderRequest);
		
		String assetEntryId = PortalUtil.getOriginalServletRequest(request).getParameter("assetEntryId");
		
		List<MainAsset> mainAssets = AssetUtil.getMainAssetsByTagName(CustomAssetDisplayPortletKeys.TAG_NAME, themeDisplay);

		renderRequest.setAttribute("mainAssets", mainAssets);
		
		renderRequest.setAttribute("assetEntryId", assetEntryId);

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);

		String resourceId = resourceRequest.getResourceID();
		
		if ("getRelatedAssetsResourceId".contentEquals(resourceId)) {

			long assetEntryId = ParamUtil.getLong(resourceRequest, "assetEntryId", 0L);

			List<RelatedAsset> relatedAssets = AssetUtil.getRelatedAssetsbyAssetEntryId(assetEntryId, themeDisplay);

			String relatedAssestAsJson = AssetUtil.getJson(relatedAssets);

			ResponseWriterUtil.write(resourceResponse, relatedAssestAsJson);
		}
		super.serveResource(resourceRequest, resourceResponse);

	}
	
}