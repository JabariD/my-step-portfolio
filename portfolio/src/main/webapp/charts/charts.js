// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Currently keeping track of:
// Countries: global, usa, china, india, brazil, mexico, russia, france, spain, pakistan
// Characteristics: New Confirmed, Total Confirmed, NewDeaths, TotalDeaths 

// NOTE: A summary of new and total cases per country updated daily. 

async function getData() {
    try {
        const response = await fetch("https://api.covid19api.com/summary");
        data = await response.json();
        // Set date
        const date = data.Date;

        let array = [];

        // Push key
        const key = ['ID', 'New Confirmed Cases Per Day', 'New Deaths Per Day', 'Total Confirmed Cases', 'Total Deaths'];
        array.push(key);

        // Countries we're getting data from! These refer to the country codes based on the API.
        const CountriesIndex = [177, 35, 76, 23, 109, 138, 59, 156, 126, 93, 176];

        CountriesIndex.map(index => {
            let country = [data.Countries[index].CountryCode, data.Countries[index].NewConfirmed, data.Countries[index].NewDeaths, data.Countries[index].TotalConfirmed, data.Countries[index].TotalDeaths];
            array.push(country);
        });
        
        loadDataToChart(array, date);

        
    } catch (e) {
        console.log("Failed to load Charts. Try to reload the page " + e);
    }
}

getData();

/** Google Charts API. Loads data and mounts it onto chart-container. */
function loadDataToChart(array, date) {

    google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(drawSeriesChart);

    /** Creates a chart and adds it to the page. */
    function drawSeriesChart() {

        var data = google.visualization.arrayToDataTable(array);

        const options = {
            title: 'Most populous countries COVID-19 statistics. The current date is: ' + date,
            hAxis: {title: 'New Confirmed Cases Per Day'},
            vAxis: {title: 'New Deaths Per Day'},
            bubble: {textStyle: {fontSize: 11}}      
        };

        var chart = new google.visualization.BubbleChart(document.getElementById('chart-container'));
        chart.draw(data, options);
    }
}