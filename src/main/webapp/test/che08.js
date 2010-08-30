Ext.namespace('zyd.api');
zyd.api.config = {
    PageSize : 50,
    layerInfo : [ [ 'com.zuiyidong.layer.restaurant', 'restaurant' ], [ 'House', 'House' ] ],
    init : function() {
        this.layerMapping = {

            'com.zuiyidong.layer.restaurant' : new zyd.api.RestaurantApi(),

            'House' : new zyd.api.HouseApi()
        };
    }
};

zyd.api.Api = function() {
}
Ext.apply(zyd.api.Api.prototype, {
    /* contains an array of string, or object like {displayName, name} */
    columns : [],

    /* returns a url, without host, that will be used to process the request */
    getRequestUrl : function() {
    },

    /* get the base parameters go with every request */
    getBaseParameter : function() {
    },

    /* 
     * renders a info window to display in gmap, 
     * should return a dom object or a html string 
     * that will be put into google map info window
     */
    generateContentForInfoWindow : function(object) {
        var r = [];
        r[r.length] = '<table style="margin:20px;">';
        for ( var i in object) {
            r[r.length] = '<tr><td>';
            r[r.length] = i;
            r[r.length] = '</td><td>';
            r[r.length] = object[i];
            r[r.length] = '</tr>';
        }
        r[r.length] = '</table>';
        return r.join('');
    },

    getStore : function() {
        var store = new Ext.data.Store( {
            autoLoad : true,
            remoteSort : true,
            url : this.getRequestUrl(),
            restful : true,
            baseParams : this.getBaseParameter(),
            reader : new Ext.data.XmlReader( {
                record : 'object',
                id : 'id'
            }, this.columns),
            paramNames : {
                start : 'start',
                sort : 'orderBy',
                limit : 'count',
                dir : 'order'
            },
            getTotalCount : function() {
                return 9000000;
            }
        });
        return store;
    },

    getColumnModel : function() {
        var columns = this.columns;
        var cs = [];
        for ( var i = 0; i < columns.length; i++) {
            var c = columns[i];
            if (typeof c == 'string') {
                cs.push( {
                    header : c
                });
            } else {
                cs.push( {
                    header : c.displayName,
                    id : c.name
                });
            }
        }
        return new Ext.grid.ColumnModel( {
            columns : cs,
            defaults : {
                sortable : true
            }
        });

    }
});

zyd.api.HouseApi = Ext.extend(zyd.api.Api, {
    columns : [ 'lng', 'lat', 'price', 'id', 'rentalType', 'subRentalType', 'paymentType', 'priceUnit', 'size', 'houseType', 'address', 'city',
            'district1', 'district3', 'district5', 'tel', 'contact', 'photo', 'floor', 'totalFloor', 'isAgent', 'agentPhoto', 'equipment',
            'decoration', 'ok', 'createTime', 'updateTime', 'link', 'description1', 'description2' ],

    getRequestUrl : function() {
        return '/service/che08b'
    },
    getBaseParameter : function() {
        return {
            objectid : 'House'
        }
    },
    generateContentForInfoWindow : function(object) {
        var r = [];
        r[r.length] = '<table style="margin:20px;" cellpadding=5>';
        for ( var i in object) {
            var v = object[i];
            if (i == 'agentPhoto' && v && v.length > 0) {
                v = '<img src="/service/image?name=' + v + '"></img>'
            } else if (i == 'photo' && v && v.length > 0) {
                v = v.split(';');
                var t = [];
                for ( var j = 0; j < v.length; j++) {
                    t[t.length] = '<img src="/service/image?name=' + v[j] + '"></img>';
                }
                v = t.join(' ');
            }
            r[r.length] = '<tr><td>';
            r[r.length] = i;
            r[r.length] = '</td><td>';
            r[r.length] = v;
            r[r.length] = '</tr>';
        }
        r[r.length] = '</table>';
        return r.join('');
    }
});

zyd.api.WifiApi = Ext.extend(zyd.api.Api, {
    columns : [ 'lng', 'lat', 'id', 'description' ],
    getRequestUrl : function() {
        return '/service/che08a'
    },
    getBaseParameter : function() {
        return {
            layer : 'com.zuiyidong.layer.wifi'
        }
    }
});
zyd.api.RestaurantApi = Ext.extend(zyd.api.Api, {
    columns : [ 'id', 'lng', 'lat', 'shopName', 'shopName2', 'city', 'address', 'district1', 'district2', 'district3', 'tel', 'tel2', 'areaCode',
            'categoryList', 'nearBy' ],
    getRequestUrl : function() {
        return '/service/che08a'
    },
    getBaseParameter : function() {
        return {
            layer : 'com.zuiyidong.layer.restaurant',
            lng : '0,1000',
            lat : '0,1000'
        }
    }
});

zyd.api.WifiApi = Ext.extend(zyd.api.Api, {
    columns : [ 'lng', 'lat', 'id', 'description' ],
    getRequestUrl : function() {
        return '/service/che08a'
    },
    getBaseParameter : function() {
        return {
            layer : 'com.zuiyidong.layer.wifi'
        }
    }
});

zyd.api.BusLineApi = Ext.extend(zyd.api.Api, {
    columns : [ 'id', 'name', 'city', 'description', 'updateTime' ],
    getRequestUrl : function() {
        return '/service/che08a'
    },

    getBaseParameter : function() {
        return {
            layer : 'com.zuiyidong.layer.busline'
        }
    }
});
zyd.api.BusStationApi = Ext.extend(zyd.api.Api, {
    columns : [ 'id', 'lng', 'lat', 'busId', 'description', 'seq', 'busName', 'city', 'stopName' ],
    getRequestUrl : function() {
        return '/service/che08a'
    },

    getBaseParameter : function() {
        return {
            layer : 'com.zuiyidong.layer.busstation'
        }
    }
});

zyd.api.config.init();
zyd.api.Explorer = {
    api : null,
    initGoogleMap : function() {
        var latlng = new google.maps.LatLng(-34.397, 150.644);
        var myOptions = {
            zoom : 12,
            center : latlng,
            mapTypeId : google.maps.MapTypeId.ROADMAP
        };
        window.map = new google.maps.Map(document.getElementById("map"), myOptions);
    },

    addMarker : function(lng, lat, content) {
        var p = new google.maps.LatLng(lat, lng);
        var marker = new google.maps.Marker( {
            position : p,
            map : window.map,
            title : 'lng:' + lng + ', lat:' + lat
        });
        window.map.setCenter(p);
        this.showInfoWindow(marker, content);
        var c = content;
        google.maps.event.addListener(marker, 'click', function() {
            zyd.api.Explorer.showInfoWindow(marker, c);
        });
    },

    showInfoWindow : function(marker, content) {
        var infoWin = zyd.api.Explorer.infoWin;
        if (infoWin == null) {
            infoWin = new google.maps.InfoWindow();
            zyd.api.Explorer.infoWin = infoWin;
        }
        infoWin.setOptions( {
            content : content
        });
        infoWin.open(window.map, marker);
    },

    buidlGridPanel : function() {
        this.gridPagingBar = new Ext.PagingToolbar( {
            pageSize : zyd.api.config.PageSize,
            displayInfo : true,
            displayMsg : 'Displaying  {0} - {1} of {2}',
            emptyMsg : "No data"
        });
        this.grid = new Ext.grid.GridPanel( {
            store : new Ext.data.Store(),
            bbar : this.gridPagingBar,
            columns : [],
            sm : new Ext.grid.RowSelectionModel( {
                singleSelect : true,
                listeners : {
                    rowselect : function(sm, row, rec) {
                        var data = rec.data;
                        if (data.lng && data.lat) {
                            var content = zyd.api.Explorer.api.generateContentForInfoWindow(data);
                            zyd.api.Explorer.addMarker(data.lng, data.lat, content);
                        }
                    }
                }
            })
        });
    },

    buildApiPanel : function() {
        var store = new Ext.data.ArrayStore( {
            fields : [ 'layer', 'displayName' ],
            data : zyd.api.config.layerInfo
        });
        this.layerCombo = new Ext.form.ComboBox( {
            displayField : 'displayName',
            valueField : 'layer',
            editable : false,
            autoSelect : true,
            mode : 'local',
            triggerAction : 'all',
            store : store,
            listeners : {
                scope : zyd.api.Explorer,
                'select' : function(box, r, index) {
                    var layer = r.data.layer;
                    var api = zyd.api.config.layerMapping[layer];
                    zyd.api.Explorer.setApi(api);
                }
            }
        });
        this.apiPanel = new Ext.Panel( {
            width : 400,
            items : [ this.layerCombo ]
        });
    },

    buildCenter : function() {
        this.buidlGridPanel();
        this.buildApiPanel()
        this.grid.region = 'center';
        this.apiPanel.region = 'east';

        this.center = new Ext.Panel( {
            layout : 'border',
            items : [ this.grid, this.apiPanel ]
        });
    },

    buildLayout : function() {
        this.north = new Ext.Panel( {
            id : 'mapPanel',
            html : '<div id=map style="width:100%;height:100%;"></div>',
            region : 'north',
            height : 450
        });

        this.buildCenter();
        this.center.region = 'center';

        new Ext.Viewport( {
            layout : 'border',
            items : [ this.north, this.center ]
        });
    },

    init : function() {
        this.buildLayout();
        this.initGoogleMap();
        this.selectFirstLayer();
    },

    setApi : function(api) {
        var store = api.getStore();
        var columnModel = api.getColumnModel();
        this.grid.reconfigure(store, columnModel);
        this.gridPagingBar.bindStore(store);
        this.api = api;
        store.load( {
            params : {
                start : 0
            }
        });
        this.store = store;
    },
    selectFirstLayer : function() {
        setTimeout(function() {
            var layer = 'House';
            var api = zyd.api.config.layerMapping[layer];
            zyd.api.Explorer.setApi(api);
            zyd.api.Explorer.layerCombo.setValue(layer);
        }, 100);
    }

}

Ext.onReady(function() {
    zyd.api.Explorer.init();
    zyd.api.Explorer.selectFirstLayer();
});