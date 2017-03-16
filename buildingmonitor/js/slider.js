/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


$('#temp-slider').slider({
    formatter: function(value) {
        return 'Current value: ' + value;
    }
});

// date range pcker
$(document).ready( function () {
    $('input[name="daterange"]').datepicker({
        orientation: "auto"
    }).on("changeDate", function(e) {
        var endDate = new Date(e.date);
        endDate.setHours(endDate.getHours() + 24);
        var fromDate = e.date;
        var data = custom.functions.getProviderData("ORG_WSO2_FLOOR_DEVICE_SENSORSTREAM", fromDate.getTime(), endDate.getTime());
        if (data) {
            heatMapManagement.functions.updateHistoricalData(data);
        }
    });
});


$(document).ready(function(){
    $("#historic-toggle").click(function(){
        heatMapManagement.functions.enableDisableHistoricalData();
        $(".date-picker").slideToggle("slow");
    });
});


