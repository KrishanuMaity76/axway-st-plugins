pluginRegister.setPluginComponent(new AzureBlobPluginSite());

function AzureBlobPluginSite() {
  function load(accountName, siteId) {
    if (siteId) {
      $.ajax({
        url : "/api/v1.4/sites/" + siteId,
        type: 'GET',
        dataType : 'json',
        cache: false,
        success : function(data) {
          if (data) {
            $.map( data, function( value, key ) {
              if (key.match("Check$")) {
                                    // regular check boxes
                                    var checkedValue = value == 'true' ? true : false;
                                    $('#' + key).prop('checked', checkedValue);
                                } else if (key == "azurePatternType") {
                                    if (value.match("regex")) {
                                        $('#patternTypeRegex').prop('checked', true);
                                    } else {
                                        $('#patternTypeGlob').prop('checked', true);
                                    }
              } else if (key == 'azureNetworkZone') {
                custom_fetchDmzZones('azureNetworkZone', value); // load zones with selected one
                $('#' + key).val(value);
              } else if ($('#' + key)) {
                $('#' + key).val(value);
              } else {
                $('#' + key).val(value);
              }
            });
          }
        }
      });
    } else {
      custom_fetchDmzZones('azureNetworkZone', 'none'); // load zones with default selected
      //disabledInputFieldsByDefault();
    }
  }

  function save(siteData, callback) {
    alert("[DEBUG]: Transfer Site data: " + $('#azureContainer').val() + ": " + $('#azureAccountName').val());
    setCheckBox("azureAutoCreateContainerCheck");
    if (siteData) {
      var customProperties = {
          "azureAccountName"              : $('#azureAccountName').val(),
          "azureAccountKey"               : $('#azureAccountKey').val(),
  				"azureContainer"                : $('#azureContainer').val(),
  				"azureAutoCreateContainerCheck" : $('#azureAutoCreateContainerCheck').val(),
  				"azureNetworkZone"              : $('#azureNetworkZone').val(),
  				"azureDownloadObjectKey"        : $('#azureDownloadObjectKey').val(),
  				"azurePatternType"              : $('input:radio[name=azurePatternType]:checked').val(),
  				"azureDownloadPattern"          : $('#azureDownloadPattern').val(),
  				"azureUploadDestination"        : $('#azureUploadDestination').val(),
  				"azureUploadMode"               : $('#azureUploadMode').children(":selected").val(),
  				"azureStorageMetadata"          : $('#azureStorageMetadata').val(),
          "azureCustomMetadata"           : $('#azureCustomMetadata').val()
				},
				siteUrl = "/api/v1.4/sites",
				siteId = siteData.id,
				data = null;
        var validationErr = [];

        if (validationErr && validationErr.length > 0) {
                var message = '';
                for(var i = 0; i < validationErr.length; i++) {
                    message += "\n" + validationErr[i];
                }
                if (callback) {
                    callback(message);
                    return;
                }
            }

            $.extend( siteData, customProperties );

            if (siteId) {

                siteUrl += '/' + siteId;
                data = siteData;
            } else {
            	data = {'sites': [siteData]};
                alert("[DEBUG]: Received data: " + data);
            }
            $.ajax({
                    url : siteUrl,
                    type: 'POST',
                dataType: 'json',
             contentType: 'application/json',
                    data: JSON.stringify(data),
                 success: function() {
                            if (callback) {
                                callback();
                            }
                          },
                   error: function(jqXHR, textStatus, errorThrown) {
                            // show 'validationErrors' or 'message' key values
                            var         errorMsg = '',
                                         jsonDoc = jQuery.parseJSON(jqXHR.responseText),
                                validationErrors = jsonDoc["validationErrors"],
                                         message = jsonDoc["message"];

                            if (validationErrors && validationErrors.length > 0) {
                                for(var i = 0; i < validationErrors.length; i++) {
                                    errorMsg += "\n" + validationErrors[i];
                                }
                            } else if (message) {
                                errorMsg = message;
                            }

                            if (callback) {
                                callback(errorMsg);
                            }
                        }
            });
        }
    }

    function setCheckBox(elementId) {
        var targetElement = $('#' + elementId);
        if (targetElement.prop('checked')) {
            targetElement.val("true");
        } else {
            targetElement.val("false");
        }
    }


    var instance = new PluginComponentInterface();
    instance.load = load;
    instance.save = save;

    return Object.seal(instance);
}


    /**
     * [BEGIN]  dmzZone functions
     */
    /* Retrieves all DMZ zones from database */
    function custom_fetchDmzZones(selectId, custom_selectedZone) {
        custom_appendDefaultOptions(selectId, custom_selectedZone);

        $.ajax({
            url : "/api/v1.0/zones",
            type: 'GET',
            dataType : 'json',
            cache: false,
            success : function(data) {
                var zonesList = data.zones;
                if (zonesList && zonesList.length > 0) {
                    custom_fillDmzZonesSelect(selectId, custom_selectedZone, zonesList);
               }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                alert("Error occurred on retrieving Network Zone.");
            }
        })
    }

    /* fills specified <select> with list of zones and select specified zone */
    function custom_fillDmzZonesSelect(selectId, selectedZone, zonesList) {
       for (var i = 0; i < zonesList.length; i++) {
           var zone = zonesList[i].zone;
           // if not the default zone, add it into the list.
           if ('Private' != zone.name) {
               custom_appendOption(selectId, selectedZone, zone.name);
           }
       }
    }

    function custom_appendDefaultOptions(selectId, selectedZone) {
        custom_appendOption(selectId, selectedZone, 'none');
        custom_appendOption(selectId, selectedZone, 'any');
        custom_appendOption(selectId, selectedZone, 'Default');
    }

    /* append <option> to specified <select> */
    function custom_appendOption(selectId, selectedElement, value) {
       if (selectedElement == value) {
           $('<option value="' + custom_htmlescape(value) + '" selected="selected">' + value + '</option>').appendTo($('#' + selectId));
       } else {
           $('<option value="' + custom_htmlescape(value) + '">' + value + '</option>').appendTo($('#' + selectId));
       }
    }

    /* Escape script characters as <&"\> */
    function custom_htmlescape(str) {
        var ret = str;
        if (str) {
            var regexp = /&/g;
            ret = ret.replace(regexp, "&amp;");
            regexp = /</g;
            ret = ret.replace(regexp, "&lt;");
            regexp = />/g;
            ret = ret.replace(regexp, "&gt;");
            regexp = /\"/g;
            ret = ret.replace(regexp, "&quot;");
            regexp = /\\/g;
            ret = ret.replace(regexp, "&#092;");
        }
        return ret;
    }
    /**
     * [END]  dmzZone functions
     */
