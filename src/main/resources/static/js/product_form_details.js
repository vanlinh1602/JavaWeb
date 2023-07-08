$(document).ready(function() {		
	$("a[name='linkRemoveDetail']").each(function(index) {
		$(this).click(function() {
			removeDetailSectionByIndex(index);
		});
	});
	
});

function addNextDetailSection() {
	allDivDetails = $("[id^='divDetail']");
	divDetailsCount = allDivDetails.length;
	
	htmlDetailSection = `
		<div class="row" id="divDetail${divDetailsCount}">
		<input type="hidden" name="detailIDs" value="0" />
		  <div class="col">
		    <input type="text" class="form-control" placeholder="Name" name="detailNames">
		  </div>
		  <div class="col">
		    <input type="text" class="form-control" placeholder="Value" name="detailValues">
		  </div>
	</div>
	`;
	
	$("#divProductDetails").append(htmlDetailSection);

	previousDivDetailSection = allDivDetails.last();
	previousDivDetailID = previousDivDetailSection.attr("id");
	 	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle icon-red"
			href="javascript:removeDetailSectionById('${previousDivDetailID}')"
			title="Remove this detail"></a>
	`;
	
	previousDivDetailSection.append(htmlLinkRemove);
	
	$("input[name='detailNames']").last().focus();
}

function removeDetailSectionById(id) {
	$("#" + id).remove();
}

function removeDetailSectionByIndex(index) {
	$("#divDetail" + index).remove();	
}