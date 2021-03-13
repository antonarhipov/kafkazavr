const uuid = guid();

window.onload = function () {

    let map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/streets-v11',
        zoom: 14
    });

    let directions = new MapboxDirections({
        accessToken: mapboxgl.accessToken,
        interactive: false,
        profile: 'driving',
        controls: {
            inputs: false,
            instructions: false
        }
    });

    directions.on('route', function (event) {
        let route = event.route[0];

        ws.send(JSON.stringify({driver: uuid, rider: rider, route: route}));

        // start driving
        let metersTraveled = 0;
        let metersPerSecond = route.distance / route.duration;
        
        // flip from latLng to lngLat
        let coordinates = decode(route.geometry).map(function (latLng) {
            var lngLat = [latLng[1], latLng[0]];
            return lngLat;
        });
        var line = {type: 'Feature', geometry: {'type': 'LineString', 'coordinates': coordinates}};

        var drivingInterval = window.setInterval(function () {
            if (metersTraveled >= route.distance) {
                window.clearInterval(drivingInterval);
            } else {
                var along = turf.along(line, metersTraveled / 1000, 'kilometers');
                marker.setLngLat(along.geometry.coordinates);
                metersTraveled = metersTraveled + metersPerSecond;
            }
        }, 1000);
    });

    map.addControl(directions);
    
    navigator.geolocation.getCurrentPosition(function (position) {
        let el = document.createElement('i');
        el.className = 'marker icon ion-model-s';
        marker =
            new mapboxgl.Marker(el, {offset: [-12, -24]}).setLngLat([position.coords.longitude, position.coords.latitude])
                .addTo(map);
        map.setCenter([position.coords.longitude, position.coords.latitude]);
    });

    let app = new Vue({
        el: '#app',
        data: {
            uuid: uuid
        }
    });
}

