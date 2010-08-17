package com.zyd.core.objecthandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import com.zyd.core.db.HibernateUtil;
import com.zyd.core.dom.DatabaseColumnInfo;
import com.zyd.core.imagestore.ImageStore;
import com.zyd.core.util.SpringContext;
import com.zyd.linkmanager.Link;

@SuppressWarnings("unchecked")
public abstract class Handler {
    private static Logger logger = Logger.getLogger(Handler.class);

    private ImageStore imageStore;

    public abstract String getEntityName();

    /**
     * Called just before converting string parameter to db types. 
     * Shuold do parameter check, and change any invalid parameter to valid formats.
     * @return false if parameter check failed and should cancel creation.
     */
    protected abstract boolean beforeCreate(HashMap values);

    public Handler() {
        imageStore = (ImageStore) SpringContext.getContext().getBean("imageStore");
    }

    /**
     * Creates an object, returns the created object.
     * The contract here is,
     * 
     * @param values contains a map of  
     *      parameter name -> parameter value
     * where the parameter name is the same as those in the database table, or to be exact, the fields in the hibernate mapping files.
     * parameter value should be a string. 
     * 
     * values must contain several system wide parameters.
     *  "objectid" identifies the object to create, should be the same as the one defined in hibernate mapping files.
     * 
     * And these values will be automatically added, so you don't have to add it again.
     * createTime, updateTime, link as an long refering to tables in link store.
     *  
     * What happens when calling this method is that the values are first normalized by calling normalizeValues(), where values are normalized 
     * from strings to various database format, like date, integer, long etc...
     * Then calling hibernate, with the provided values and "objectid", saving object into database.
     *
     * @return false if failed to create.
     */
    public Object create(HashMap values) {
        if (beforeCreate(values) == false) {
            return false;
        }
        String entityName = (String) values.get(Parameter.PARAMETER_OBJECT_ID);
        ObjectHelper.nomorlizedParameters(values, HibernateUtil.getTableMetaData(HibernateUtil.getTableName(entityName)));
        Date now = new Date();
        values.put(Columns.CreateTime, now);
        values.put(Columns.UpdateTime, now);
        Object o = values.remove(Columns.Link);
        if (o != null) {
            values.put(Columns.Link, ((Link) o).getId());
        }
        processImages(values);
        try {
            HibernateUtil.saveObject(entityName, values);
        } catch (Throwable e) {
            logger.error("Failed saving ojbect", e);
            logger.debug("Values trying to save are:" + values.toString());
            return false;
        }
        return true;
    }

    /**
     * TODO: when error happened, should cancel and delete all created images
     * @param values
     */
    private void processImages(HashMap values) {
        String imageCount = (String) values.get(Parameter.PARAMETER_IMAGECOUNT);
        if (imageCount == null) {
            return;
        }
        for (int i = 0, len = imageCount.length(); i < len; i++) {
            if (Character.isDigit(imageCount.charAt(i)) == false) {
                logger.warn("Client posted invalid imageCount :" + imageCount);
                return;
            }
        }
        int count = Integer.parseInt(imageCount);
        if (count <= 0) {
            return;
        }
        // image field - > ';' separated image uuids
        HashMap<String, StringBuffer> imageFieldMap = new HashMap<String, StringBuffer>();
        ArrayList<String> imageUids = new ArrayList<String>(count);
        boolean hasError = false;
        for (int i = 0; i < count; i++) {
            String imageDataParameterName = Parameter.PARAMETER_IMAGE_DATA_PREFIX + i;
            String imageSuffixParameterName = Parameter.PARAMETER_IMAGE_SUFFIX_PREFIX + i;
            String imageFieldParameterName = Parameter.PARAMETER_IMAGEFIELD_PREFIX + i;

            String imageData = (String) values.remove(imageDataParameterName);
            String imageSuffix = (String) values.remove(imageSuffixParameterName);
            String imageField = (String) values.remove(imageFieldParameterName);

            if (imageData == null || imageSuffix == null || imageField == null) {
                logger.warn("Can not upload image, imageData or imageSuffix or imageField is null, canceling");
                hasError = true;
                break;
            }
            try {
                String imageUuid = imageStore.storeImage(imageData, imageSuffix);
                imageUids.add(imageUuid);
                StringBuffer uidBuffer = imageFieldMap.get(imageField);
                if (uidBuffer == null) {
                    uidBuffer = new StringBuffer();
                    imageFieldMap.put(imageField, uidBuffer);
                }
                uidBuffer.append(imageUuid);
                uidBuffer.append(';');
            } catch (IOException e) {
                logger.warn("Can not upload image, total image count: " + count + ", current index: " + i + ", imageSuffix:" + imageSuffix + ", imageData:" + imageData.substring(0, 100));
                e.printStackTrace();
                hasError = true;
                break;
            }
        }
        if (hasError) {
            for (String s : imageUids) {
                ImageStore.deleteImageByName(s);
            }
            imageFieldMap.clear();
        } else {
            if (imageUids.size() != count) {
                logger.warn("Can not create image, imageCount is not the same as passed imageCount: " + count + ":" + imageUids.size());
                return;
            }
            Set<String> fields = imageFieldMap.keySet();
            for (String imageField : fields) {
                StringBuffer buf = imageFieldMap.get(imageField);
                buf.deleteCharAt(buf.length() - 1);
                values.put(imageField, buf.toString());
            }
        }
    }

    public SearchResult query(HashMap params) {
        String entityName = (String) params.get(Parameter.PARAMETER_OBJECT_ID);
        String tableName = HibernateUtil.getTableName(entityName);
        if (tableName == null) {
            return null;
        }
        HashMap<String, DatabaseColumnInfo> meta = HibernateUtil.getTableMetaData(tableName);
        return ObjectHelper.defaultQuery(params, entityName, meta);
    }

    /**
     * make sure the key specified in columns, exist in values
     * @return null if nothing is missing, or the missing column name
     */
    protected static String checkColumnExistence(String[] columns, HashMap values) {
        for (String c : columns) {
            if (values.containsKey(c) == false) {
                return c;
            }
        }
        return null;
    }

    public final static class Parameter {
        /* start index of the record */
        public final static String PARAMETER_START = "start";

        /* how many to fetch, page size */
        public final static String PARAMETER_COUNT = "count";
        public final static String PARAMETER_ORDER_BY = "orderBy";
        /* set this parameter to true to skip check url, i.e. url does not have to come from server, can be out of processing list. */
        public final static String PARAMETER_SKIP_URL_CHECK = "skipUrlCheck";
        public final static String PARAMETER_ORDER = "order";
        public final static String PARAMETER_VALUE_ORDER_ASC = "asc";
        public final static String PARAMETER_VALUE_ORDER_DESC = "desc";
        public final static String PARAMETER_OBJECT_ID = "objectid";
        public final static String PARAMETER_SEPARATOR = "separator";
        public final static String PARAMETER_SEPARATOR_DEFAULT_VALUE = "-";
        protected final static Integer PARAMETER_VALUE_OK_YES = new Integer(1);
        protected final static Integer PARAMETER_VALUE_OK_NO = new Integer(0);

        public final static String PARAMETER_IMAGECOUNT = "imageCount";
        public final static String PARAMETER_IMAGEFIELD_PREFIX = "imageField";
        public final static String PARAMETER_IMAGE_DATA_PREFIX = "imageData";
        public final static String PARAMETER_IMAGE_SUFFIX_PREFIX = "imageSuffix";
    }

    /**
     * These are columns for every object
     */
    public static class Columns {
        public final static String ID = "id";
        public final static String Lng = "lng";
        public final static String Lat = "lat";
        /*
         * The refering url from where this object is created.
         */
        public final static String Link = "link";
        public final static String CreateTime = "createTime";
        public final static String UpdateTime = "updateTime";
    }
}
