Ext.namespace('zyd.api');
zyd.api.config = {
    PageSize : 50,
    layerInfo : [ [ 'House', 'House' ], [ 'Film', 'Film' ], [ 'Weather', 'Weather' ], [ 'com.zuiyidong.layer.wifi', 'Wifi' ],
            [ 'com.zuiyidong.layer.busstation', 'Busstation' ], [ 'com.zuiyidong.layer.busline', 'Busline' ],
            [ 'com.zuiyidong.layer.restaurant', 'Restaurant' ] ],
    init : function() {
        this.layerMapping = {
            'com.zuiyidong.layer.wifi' : new zyd.api.WifiApi(),
            'com.zuiyidong.layer.restaurant' : new zyd.api.RestaurantApi(),
            'com.zuiyidong.layer.busline' : new zyd.api.BusLineApi(),
            'com.zuiyidong.layer.busstation' : new zyd.api.BusStationApi(),
            'House' : new zyd.api.HouseApi(),
            'Film' : new zyd.api.FilmApi(),
            'Weather' : new zyd.api.WeatherApi()
        };
    }
};

zyd.api.Api = function() {
}

Ext.apply(zyd.api.Api.prototype, {
    columns : [],
    queriableColumns : [],
    requestUrl : '',
    baseParameter : {},
    isHasLocation : true,
    getRequestUrl : function() {
        return this.requestUrl;
    },
    getBaseParameter : function() {
        return this.baseParameter;
    },
    hasLocation : function() {
        return this.isHasLocation;
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
    /**
     * return column names only, in an array
     */
    getColumnNames : function() {
        var cs = this.columns;
        var r = [];
        for ( var i = 0; i < cs.length; i++) {
            var c = cs[i];
            if (typeof c == 'string')
                r.push(c);
            else
                r.push(c.name);
        }
        return r;
    },
    getQueriableColumns : function() {
        return this.queriableColumns;
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
            }, this.getColumnNames()),
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
        return store;
    },

    getColumnModel : function() {
        var columns = this.getColumnNames();
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
    columns : [ 'id', 'description1', 'lng', 'lat', 'price', 'rentalType', 'subRentalType', 'paymentType', 'priceUnit', 'size', 'houseType',
            'address', 'city', 'district1', 'district3', 'district5', 'tel', 'contact', 'floor', 'totalFloor', 'isAgent', 'photo', 'agentPhoto',
            'equipment', 'decoration', 'createTime', 'updateTime', 'link', 'description2' ],
    queriableColumns : [ 'id', {
        name : 'lng',
        type : 'number'
    }, {
        name : 'lat',
        type : 'number'
    }, {
        name : 'price',
        type : 'number'
    }, 'rentalType', {
        name : 'size',
        type : 'number'
    }, 'city', 'district1', 'tel', 'contact' ],
    requestUrl : '/service/object',
    baseParameter : {
        objectid : 'House',
        count : zyd.api.config.PageSize
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

zyd.api.FilmApi = Ext.extend(zyd.api.Api, {
    isHasLocation : false,
    columns : [ 'id', 'theaterName', 'city', 'name', 'description', 'showTime', 'showDate', 'createTime', 'updateTime' ],
    queriableColumns : [ 'id', 'theaterName', 'city', 'name', {
        name : 'showDate',
        type : 'dateRange'
    } ],
    requestUrl : '/service/object',
    baseParameter : {
        objectid : 'Film',
        count : zyd.api.config.PageSize
    }
});

zyd.api.WeatherApi = Ext.extend(zyd.api.Api, {
    isHasLocation : false,
    columns : [ 'id', 'locationId', 'castDate', 'condition0', 'temp0', 'wind0', 'strength0', 'condition1', 'temp1', 'wind1', 'strength1',
            'createTime', 'updateTime' ],
    queriableColumns : [ 'id', 'locationId', {
        name : 'castDate',
        type : 'dateRange'
    } ],
    requestUrl : '/service/object',
    baseParameter : {
        objectid : 'Weather',
        count : zyd.api.config.PageSize
    }
});
zyd.api.WifiApi = Ext.extend(zyd.api.Api, {
    columns : [ 'id', 'lng', 'lat', 'name', 'city', 'province', 'isp', 'type', 'address', 'description' ],
    queriableColumns : [ 'id', {
        name : 'lng',
        type : 'number'
    }, {
        name : 'lat',
        type : 'number'
    } ],
    requestUrl : '/service/api',
    baseParameter : {
        layer : 'com.zuiyidong.layer.wifi',
        count : zyd.api.config.PageSize
    }
});

zyd.api.RestaurantApi = Ext.extend(zyd.api.Api, {
    columns : [ 'id', 'lng', 'lat', 'city', 'shopName', 'shopName2', 'address', 'district1', 'district2', 'district3', 'tel', 'tel2', 'areaCode',
            'categoryList', 'nearBy' ],
    queriableColumns : [ 'id', {
        name : 'lng',
        type : 'number'
    }, {
        name : 'lat',
        type : 'number'
    }, 'shopName', 'city' ],
    requestUrl : '/service/api',
    baseParameter : {
        layer : 'com.zuiyidong.layer.restaurant',
        count : zyd.api.config.PageSize
    }
});

zyd.api.BusLineApi = Ext.extend(zyd.api.Api, {
    columns : [ 'id', 'name', 'city', 'description', 'updateTime' ],
    queriableColumns : [ 'id', 'name', 'city' ],
    requestUrl : '/service/api',
    isHasLocation : false,
    baseParameter : {
        layer : 'com.zuiyidong.layer.busline',
        count : zyd.api.config.PageSize
    }
});

zyd.api.BusStationApi = Ext.extend(zyd.api.Api, {
    columns : [ 'id', 'lng', 'lat', 'busId', 'description', 'seq', 'busName', 'city', 'stopName' ],
    queriableColumns : [ 'id', {
        name : 'lng',
        type : 'number'
    }, {
        name : 'lat',
        type : 'number'
    }, {
        name : 'busId',
        type : 'number'
    } ],
    requestUrl : '/service/api',
    baseParameter : {
        layer : 'com.zuiyidong.layer.busstation',
        count : zyd.api.config.PageSize
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
            fieldLabel : 'Layer',
            margin : 10,
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
            padding : 10,
            autoScroll : true,
            width : 350,
            items : [ this.layerCombo ]
        });
    },

    buildFormPanel : function() {
        var fields = this.api.getQueriableColumns();
        var r = [], defaults = {
            xtype : 'compositefield',
            msgTarget : 'side',
            width : 200
        };
        for ( var i = 0; i < fields.length; i++) {
            var f = fields[i];
            var obj = {};
            Ext.apply(obj, defaults);
            if (typeof f == 'string') {
                obj.fieldLabel = f;
                obj.items = [ {
                    xtype : 'textfield',
                    name : f,
                    width : 100
                }, {
                    xtype : 'checkbox',
                    name : f + '-check',
                    width : 20
                } ];
            } else {
                obj.fieldLabel = f.name;
                var xtype = 'textfield', width = 46, format = undefined;
                if (f.type == 'dateRange') {
                    xtype = 'datefield';
                    width = 80;
                    format = 'Y-m-d';
                }

                obj.items = [ {
                    xtype : xtype,
                    name : f.name + '-start',
                    width : width,
                    format : format

                }, {
                    xtype : xtype,
                    name : f.name + '-end',
                    width : width,
                    format : format
                }, {
                    xtype : 'checkbox',
                    name : f.name + '-check',
                    width : 20
                } ];
            }
            r.push(obj);
        }
        var formPanel = new Ext.form.FormPanel( {
            width : 300,
            padding : 10,
            items : r,
            border : false,
            buttons : [ {
                text : 'Clear',
                handler : function() {
                    var exp = zyd.api.Explorer;
                    exp.store.baseParams = exp.api.getBaseParameter();
                    exp.store.load();
                    formPanel.form.reset();
                }
            }, {
                text : 'Search',
                handler : function() {
                    var obj = {};
                    Ext.iterate(formPanel.form.getValues(), function(key, value) {
                        if (!value || value.trim().length == 0)
                            return;
                        var name;
                        if (key.endsWith('-start')) {
                            name = key.substring(0, key.length - 6);
                            if (!obj[name])
                                obj[name] = [ '2' ];
                            obj[name][2] = value;
                        } else if (key.endsWith('-end')) {
                            name = key.substring(0, key.length - 4);
                            if (!obj[name])
                                obj[name] = [ '2' ];
                            obj[name][3] = value;
                        } else if (key.endsWith('-check')) {
                            name = key.substring(0, key.length - 6);
                            /**bug here, don't know the length,  **/
                            if (!obj[name]) {
                                obj[name] = [ '1' ];
                            }
                            obj[name][1] = value;
                        } else {
                            name = key;
                            if (!obj[name])
                                obj[name] = [ '1' ];
                            obj[name][2] = value;
                        }
                    }, this);
                    var params = {};
                    for ( var name in obj) {
                        var values = obj[name];
                        var checkon = (values[1] == 'on');
                        var type = values[0];
                        if (type == '1') {
                            /** string value, with checkbox on, exact search **/
                            var value = values[2];
                            if (typeof value != 'undefined') {
                                if (!checkon) {
                                    value = '%' + value + '%';
                                }
                                params[name] = value;
                            }
                        } else if (type == '2') {
                            var start = values[2], end = values[3], value = '';
                            if (typeof start != 'undefined' && typeof end != 'undefined') {
                                value = start + ',' + end;
                            } else if (typeof start == 'undefined' && typeof end == 'undefined') {
                            } else {
                                if (checkon) {
                                    value = start || end;
                                } else {
                                    /* open ended*/
                                    if (typeof start != 'undefined') {
                                        value = start + ',';
                                    } else {
                                        value = end + ',';
                                    }
                                }
                            }
                            if (value != '')
                                params[name] = value;
                        }
                    }
                    params.start = 0;
                    params.separator = ',';
                    var exp = zyd.api.Explorer;
                    Ext.apply(params, exp.api.getBaseParameter());
                    exp.store.baseParams = params;
                    exp.store.load();
                }
            } ]
        });
        this.apiPanel.remove(this.formPanel);
        this.apiPanel.add(formPanel);
        this.apiPanel.doLayout();
        this.formPanel = formPanel;
    },

    buildCenter : function() {
        this.buidlGridPanel();
        this.buildApiPanel()
        this.grid.region = 'center';
        this.apiPanel.region = 'east';

        this.center = new Ext.Panel( {
            layout : 'border',
            defaults : {
                split : true
            },
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
            defaults : {
                split : true
            },
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
                start : 0,
                separator : ','
            }
        });
        this.store = store;
        this.buildFormPanel();
        if (api.hasLocation()) {
            this.north.expand()
        } else {
            this.north.collapse();
        }
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

String.prototype.endsWith = function(str) {
    return (this.match(str + "$") == str)
}
String.prototype.startsWith = function(str) {
    return (this.match("^" + str) == str)
}

Ext.onReady(function() {
    zyd.api.Explorer.init();
    zyd.api.Explorer.selectFirstLayer();
});
