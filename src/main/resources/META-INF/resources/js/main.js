
function getrelatedAssets(assetEntryId, e){
	
	history.pushState(null, null, "?assetEntryId="+assetEntryId);
	if(e !== null){
		$(e).siblings().removeClass("active");
		$(e).addClass("active");
	}
    AUI().use('aui-io-request', function(A){
        A.io.request(getRelatedAssetsResourceURL, {
               method: 'post',
               data: {
            	   assetEntryId: assetEntryId
               } ,
               on: {
            	   success: function() {
            		     var assetEntries = JSON.parse(this.get('responseData'));
            		     drawRelatedAssets(assetEntries);
            	   }
               }
        });
 
    });
}

function drawRelatedAssets(assetEntries){
	var assetCards = assetEntries.map((assetEntry, index) => {
	return(
	 	`<div class="card">
	 	<div class="card-header" id="heading_${index}" data-toggle="collapse" data-target="#collapse_${index}" aria-expanded="${index == 0 ? true : false}" aria-controls="collapse_${index}">
	 	  <h2 class="mb-0">
	 	    <button class="btn btn-link" type="button">
	 	      ${assetEntry.relatedAssetTitle}
	 	    </button>
	 	  </h2>
	 	</div>
	
	 	<div id="collapse_${index}" class="collapse ${index == 0 ? 'show' : ''}" aria-labelledby="heading_${index}" data-parent="#accordionAssetCards">
	 	  <div class="card-body">
	 	    ${assetEntry.relatedAssetDescription}
	 	  </div>
	 	</div>
	 	</div>`
		)
	});
	$("#accordionAssetCards").html(assetCards);
}


// get related assets on page load
(function (){
	var url_string = window.location.href
	var url = new URL(url_string);
	var assetEntryId = url.searchParams.get("assetEntryId");
	getrelatedAssets(assetEntryId, null);
}());