<%@ include file="init.jsp" %>

<liferay-portlet:resourceURL id="getRelatedAssetsResourceId" var="getRelatedAssetsResourceURL"/>

<script>
var getRelatedAssetsResourceURL = '${getRelatedAssetsResourceURL}';
</script>
<script type="text/javascript" src="<%= renderRequest.getContextPath() %>/js/main.js"></script>
<div class="container">
	<div class="row">
		<div class="col-md-4">
			<ul class="list-group">
				<c:forEach var="mainAsset" items="${mainAssets}">
					<li class="list-group-item <c:if test = "${mainAsset.assetId == assetEntryId}"> active </c:if>" onclick="getrelatedAssets('${mainAsset.assetId}', this)" style="cursor: pointer;">
					  	${mainAsset.assetName}
					</li>
				</c:forEach>
			</ul>
		</div>
		<div class="col-md-8">
			<div class="accordion" id="accordionAssetCards">
			 
			</div>
		</div>
	</div>
</div>