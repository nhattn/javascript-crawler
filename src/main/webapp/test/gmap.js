var pageSize = 50;
var columns = [ 'lng', 'lat', 'price', 'id', 'rentalType', 'subRentalType', 'paymentType', 'priceUnit', 'size', 'houseType', 'address', 'city',
        'district1', 'district3', 'district5', 'tel', 'contact', 'photo', 'floor', 'totalFloor', 'isAgent', 'agentPhoto', 'equipment', 'decoration',
        'ok', 'createTime', 'updateTime', 'link', 'description1', 'description2' ];

function initGoogleMap() {
    var latlng = new google.maps.LatLng(-34.397, 150.644);
    var myOptions = {
        zoom : 12,
        center : latlng,
        mapTypeId : google.maps.MapTypeId.ROADMAP
    };
    window.map = new google.maps.Map(document.getElementById("map"), myOptions);

}
function addMarker(lng, lat) {
    var p = new google.maps.LatLng(lat, lng);
    var marker = new google.maps.Marker( {
        position : p,
        map : window.map,
        title : 'lng:' + lng + ', lat:' + lat
    });
    window.map.setCenter(p);
    /*
    var infowindow = new google.maps.InfoWindow( {});
    infowindow.setContent('dkddd');
    infowindow.setPosition(latlng);
    infowindow.open(map, marker);
    google.maps.event.addListener(marker, 'click', function() {
        infowindow.open(map, marker);
    });
    */
}

function getStore() {
    if (window.store)
        return window.store;

    var defaultParams = {
        objectid : 'House',
        count : pageSize
    };
    var store = new Ext.data.Store( {
        autoLoad : false,
        remoteSort : true,
        url : '/service/object',
        restful : true,
        baseParams : defaultParams,
        reader : new Ext.data.XmlReader( {
            record : 'object',
            id : 'id'
        }, columns),
        paramNames : {
            start : 'start',
            limit : 'count',
            sort : 'orderBy',
            dir : 'order'
        },
        getTotalCount : function() {
            return 9000000;
        }
    });
    window.store = store;
    return store;
}

function buidlGridPanel() {
    var headers = [];
    for ( var i = 0; i < columns.length; i++) {
        var c = columns[i];
        headers.push( {
            header : c,
            dataIndex : c,
            sortable : true
        });
    }

    var grid = new Ext.grid.GridPanel( {
        store : getStore(),
        columns : headers,
        bbar : new Ext.PagingToolbar( {
            pageSize : pageSize,
            store : getStore(),
            displayInfo : true,
            displayMsg : 'Displaying  {0} - {1} of {2}',
            emptyMsg : "No data"
        }),
        sm : new Ext.grid.RowSelectionModel( {
            singleSelect : true,
            listeners : {
                rowselect : function(sm, row, rec) {
                    var data = rec.data;
                    addMarker(data.lng, data.lat);
                }
            }
        })
    });
    return grid;
}

function buildApiPanel() {
    var r = new Ext.Panel( {
        html : 'dkdk',
        width : 400
    });
    return r;
}
function buildCenter() {
    var grid = buidlGridPanel();
    var apiPanel = buildApiPanel()

    grid.region = 'center';
    apiPanel.region = 'east';
    var center = new Ext.Panel( {
        layout : 'border',
        items : [ grid, apiPanel ]
    });
    return center;
}

function buildLayout() {
    var north = new Ext.Panel( {
        id : 'mapPanel',
        html : '<div id=map style="width:100%;height:100%;"></div>',
        region : 'north',
        height : 450
    });

    var center = buildCenter();
    center.region = 'center';

    new Ext.Viewport( {
        layout : 'border',
        items : [ north, center ]
    });
}

function loadit() {
    buildLayout();
    initGoogleMap();
    getStore().load( {
        params : {
            start : 0
        }
    });
}

Ext.onReady(loadit);