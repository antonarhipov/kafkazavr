const uuid = guid();

window.onload = function () {

    let status = 'available';

    let rider = null;
    let marker = null;
    let riderMarkers = {};
    
    let map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/streets-v11',
        zoom: 14
    });

    let directions = new MapboxDirections({
        accessToken: mapboxgl.accessToken,
        interactive: false,
        profile: 'mapbox/driving',
        controls: {
            inputs: false,
            instructions: false
        }
    });

    map.addControl(directions);

    directions.on('route', function (event) {
        let route = event.route[0];

        ws.send(JSON.stringify({
            driver: uuid,
            rider: rider,
            route: route
        }));

        // start driving
        let metersTraveled = 0;
        let metersPerSecond = route.distance / route.duration;

        // flip from latLng to lngLat
        let coordinates = decode(route.geometry).map(function (latLng) {
            return [latLng[1], latLng[0]];
        });
        
        let line = {
            type: 'Feature',
            geometry: {'type': 'LineString', 'coordinates': coordinates}
        };

        let drivingInterval = window.setInterval(function () {
            if (metersTraveled >= route.distance) {
                window.clearInterval(drivingInterval);
            } else {
                let along = turf.along(line, metersTraveled / 1000, 'kilometers');
                marker.setLngLat(along.geometry.coordinates);
                metersTraveled = metersTraveled + metersPerSecond;
            }
        }, 1000);
    });

    
    
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

    let ws = new WebSocket(wsUrl);
    ws.onmessage = function (event) {
        let data = JSON.parse(event.data);

        if (riderMarkers[data.rider] == null) {
            let el = document.createElement('i');
            el.className = 'marker icon ion-ios-body';
            el.addEventListener('click', function () {
                status = 'pickup';
                rider = data.rider;

                directions.setOrigin(marker.getLngLat().toArray());
                directions.setDestination([data.lngLat.lng, data.lngLat.lat]);

                // todo: hide other riders
            });
            riderMarkers[data.rider] = new mapboxgl.Marker(el, {offset: [-12, -24]}).setLngLat(data.lngLat).addTo(map);
        } else {
            riderMarkers[data.rider].setLngLat(data.lngLat);
        }
    };

    window.setInterval(function () {
        if ((marker != null) && (ws.readyState === 1)) {
            ws.send(JSON.stringify({
                driver: uuid,
                lngLat: marker.getLngLat(),
                status: status,
                rider: rider
            }));
        }
    }, 500);
}

