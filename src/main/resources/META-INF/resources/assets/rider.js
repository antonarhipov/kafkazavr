const uuid = guid();

window.onload = function () {

    let marker = null;
    let driverMarkers = {};

    let status = 'available';
    let driver = null;

    let ws = new WebSocket(wsUrl);

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

    map.on('click', function (e) {
        if (driver == null) {
            if (marker == null) {
                let el = document.createElement('i');
                el.className = 'marker icon ion-ios-body';
                marker = new mapboxgl.Marker(el, {offset: [-12, -24]}).setLngLat(e.lngLat).addTo(map);
            } else {
                marker.setLngLat(e.lngLat);
            }
        }
    });

    map.on('click', function (e) {
        if (driver == null) {
            if (marker == null) {
                var el = document.createElement('i');
                el.className = 'marker icon ion-ios-body';
                marker = new mapboxgl.Marker(el, {offset: [-12, -24]}).setLngLat(e.lngLat).addTo(map);
            } else {
                marker.setLngLat(e.lngLat);
            }
        }
    });

    navigator.geolocation.getCurrentPosition(function (position) {
        map.setCenter([position.coords.longitude, position.coords.latitude]);
    });

    var app = new Vue({
        el: '#app',
        data: {
            uuid: uuid
        }
    });
    
    ws.onmessage = function (event) {
        let data = JSON.parse(event.data);

        if ((data.rider === uuid) && (status !== 'pickup') && (data.lngLat != null)) {
            status = 'pickup';
            driver = data.driver;

            directions.setOrigin([data.lngLat.lng, data.lngLat.lat]);
            directions.setDestination(marker.getLngLat().toArray());

            // todo: hide other drivers
        }

        if (driverMarkers[data.driver] == null) {
            let el = document.createElement('i');
            el.className = 'marker icon ion-android-car';
            driverMarkers[data.driver] = new mapboxgl.Marker(el, {offset: [-12, -24]}).setLngLat(data.lngLat).addTo(map);
        } else {
            if (data.lngLat != null) {
                driverMarkers[data.driver].setLngLat([data.lngLat.lng, data.lngLat.lat]);
            }
        }
    };

    window.setInterval(function () {
        if ((marker != null) && (ws.readyState === 1)) {
            ws.send(JSON.stringify({rider: uuid, lngLat: marker.getLngLat(), status: status, driver: driver}));
        }
    }, 500);

};