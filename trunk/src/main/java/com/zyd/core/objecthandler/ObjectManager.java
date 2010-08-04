package com.zyd.core.objecthandler;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.objecthandler.Handler.Parameter;

@SuppressWarnings("unchecked")
public class ObjectManager {
    private static Logger logger = Logger.getLogger(ObjectManager.class);

    private final static HashMap<String, Handler> HandlerMapping = new HashMap<String, Handler>();
    private final static String PACKAGE_NAME = ObjectManager.class.getPackage().getName();
    private DefaultHandler defaultObjectHandler;

    private ObjectManager() {
        defaultObjectHandler = new DefaultHandler();
    }

    /**
     * calls appropriate handler to handle create request,
     * must specify the Config.NAME_APP_PARAMETER parameter as the object name
     * @param values
     * @return
     */
    public Object create(HashMap values) {
        String objectName = (String) values.get(Parameter.PARAMETER_OBJECT_ID);
        Handler handler = lookupObjectHandler(objectName);
        if (handler == null) {
            logger.warn("Can not find handler for object with name : " + objectName);
            return false;
        }
        Object r = handler.create(values);
        return r;
    }

    /**
     * calls appropriate handler to handle query request,
     * must specify the Config.NAME_APP_PARAMETER parameter as the object name
     * @param criteria
     * @return
     */
    public SearchResult query(HashMap criteria) {
        String objectName = (String) criteria.get(Parameter.PARAMETER_OBJECT_ID);
        Handler handler = lookupObjectHandler(objectName);
        if (handler == null) {
            logger.warn("Can not find handler for object with name : " + objectName);
            return null;
        }
        SearchResult r = handler.query(criteria);
        return r;
    }

    private Handler lookupObjectHandler(String name) {
        Handler service = null;
        if (name != null) {
            if (HandlerMapping.containsKey(name)) {
                service = HandlerMapping.get(name);
            } else {
                try {
                    String className = PACKAGE_NAME + "." + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                    service = (Handler) Class.forName(className).newInstance();
                } catch (Exception e) {
                    if (HibernateUtil.getTableName(name) != null) {
                        service = defaultObjectHandler;
                    }
                }

                if (service != null) {
                    synchronized (this) {
                        HandlerMapping.put(name, service);
                    }
                    logger.info("Object with name :" + name + " is mapped to " + service.getClass().getName());
                } else {
                    logger.info("Can not find any handler for object " + name);
                }
            }
        }
        return service;
    }

    public void deleteAllObjects() {
        //TODO: have to auto load every thing, can't hard code here.
        (new House()).deleteAll();
        (new AppLog()).deleteAll();
//        (new Bus()).deleteAll();
    }

}
