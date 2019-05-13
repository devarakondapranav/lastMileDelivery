//window.alert("Yo");
var bus_node;
bus_node = "bus_18";
function changeMap()
{
	window.alert("Setting the bus");
	if(document.getElementById("bus_18").checked)
		bus_node = "bus_18";
	if(document.getElementById("bus_17").checked)
		bus_node = "bus_17";
	myMap()
	//window.alert("Called the map function");
}

function myMap() {
	// window.alert("Jai Balayya");
	//var flag = true;
	/*
	if(document.getElementById("bus_18").checked)
		bus_node = "bus_18";
	if(document.getElementById("bus_17").checked)
		bus_node = "bus_17";
	*/



	var firebaseRef = firebase.database().ref();
var firebaseLocRef = firebase.database().ref().child(bus_node);
  var mapCanvas = document.getElementById("map");
  var map;
  var marker;

  firebaseLocRef.once("value", function(snapshot){
  	var location = snapshot.val();
  	location = location.split(",");
  	var latitude = parseFloat(location[0]);
  	//window.alert(latitude);
  	var longitude = parseFloat(location[1]);
  	var myCenter = new google.maps.LatLng(latitude,longitude); 
    var mapOptions = {center: myCenter, zoom: 15};
    
     map = new google.maps.Map(mapCanvas,mapOptions);
    		
     marker = new google.maps.Marker({
    position: myCenter,
    animation: google.maps.Animation.BOUNCE
  });
  marker.setMap(map);

  })


  firebaseLocRef.on("value", function(snapshot){
  	var location = snapshot.val();
  	location = location.split(",");
  	var latitude = parseFloat(location[0]);
  	//window.alert(latitude);
  	var longitude = parseFloat(location[1]);
  	var myCenter = new google.maps.LatLng(latitude,longitude); 
    var mapOptions = {center: myCenter, zoom: 15};
    
    //var map = new google.maps.Map(mapCanvas,mapOptions);
   	marker.setMap(null);
    marker = new google.maps.Marker({
    position: myCenter,
    animation: google.maps.Animation.BOUNCE
  });
  map.panTo(myCenter);
  marker.setMap(map);

  })


  
}