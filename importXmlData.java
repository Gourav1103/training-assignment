
//Imports neccessary packages
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.entity.util.EntityQuery;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;

public class parseXml {

    private static final String FILENAME = "C:\\Users\\91999\\OffBiz\\ofbiz-framework\\plugins\\importPies\\src\\main\\parse.xml";
    private static LocalDispatcher dispatcher;
    private static Delegator delegator;
    private static GenericValue userLogin;

    public static Map<String, Object> processXmlData(DispatchContext dctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        dispatcher = dctx.getDispatcher();
        delegator = dctx.getDelegator();
        userLogin = (GenericValue) context.get("userLogin"); // get UserLogin from Context
        try {
            // call the parsing function
            printXmlByXmlCursorReader();
            return result;
        } catch (Exception exception) {
            exception.printStackTrace();
            return result;
        }
    }

    // parsing xml data
    private static void printXmlByXmlCursorReader() {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            Path filePath = Paths.get(FILENAME);
            FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
            XMLStreamReader reader = factory.createXMLStreamReader(fileInputStream);

            while (reader.hasNext()) {
                int itemEvent = reader.next();
                if (itemEvent == XMLStreamConstants.START_ELEMENT) {
                    String currentElement = reader.getLocalName();
                    if ("Item".equals(currentElement)) {
                        processItemElement(reader); // Process the Item data (each product)
                    }
                }
            }

            reader.close();// close the reader
            fileInputStream.close();
        } catch (XMLStreamException exception) {
            exception.printStackTrace();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // Process Each Item
    private static void processItemElement(XMLStreamReader reader) {
        try {
            String maintenanceType = reader.getAttributeValue(null, "MaintenanceType");
            String partNumber = "";
            String hazardousMaterialCode = "";
            String itemLevelGTIN = "";
            String GTINQualifier = "";
            String quantityUomId = "";
            String brandAAIAID = "";
            String brandLabel = "";
            String ACESApplications = "";
            String itemQuantitySize = "";
            String quantityPerApplication = "";
            String itemEffectiveDate = "";
            String availableDate = "";
            String minimumOrderQuantity = "";
            String AAIAProductCategoryCode = "";
            String partTerminologyID = "";
            Map<String, Object> packageData = new HashMap<>();
            List<Map<String, Object>> descriptionsData = new ArrayList<>();
            List<Map<String, Object>> digitalAssetsInformation = new ArrayList<>();
            List<Map<String, Object>> partInterchangeData = new ArrayList<>();
            List<Map<String, Object>> productAttributesData = new ArrayList<>();
            List<Map<String, Object>> extendedInformationData = new ArrayList<>();

            while (reader.hasNext()) {
                int event = reader.next();
                // check the event on Start Element
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String elementName = reader.getLocalName();

                    // get the element text
                    switch (elementName) {
                        case "HazardousMaterialCode":
                            hazardousMaterialCode = reader.getElementText();
                            break;
                        case "ItemLevelGTIN":
                            GTINQualifier = reader.getAttributeValue(null, "GTINQualifier");
                            itemLevelGTIN = reader.getElementText();
                            break;
                        case "PartNumber":
                            partNumber = reader.getElementText();

                            // create a Product using PartNumber
                            Map<String, Object> productPartsData = new HashMap<>();
                            productPartsData.put("productId", partNumber);
                            productPartsData.put("productTypeId", "MARKETING_PKG_AUTO");
                            productPartsData.put("internalName", partNumber);
                            productPartsData.put("userLogin", userLogin);
                            try {
                                GenericValue product = EntityQuery.use(delegator).from("Product")
                                        .where("productId", partNumber).cache().queryOne();

                                // create a new Product
                                if (product == null) {
                                    dispatcher.runSync("createProduct", productPartsData);
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            break;
                        case "BrandAAIAID":
                            brandAAIAID = reader.getElementText();
                            break;
                        case "BrandLabel":
                            brandLabel = reader.getElementText();
                            break;
                        case "ACESApplications":
                            ACESApplications = reader.getElementText();
                            break;
                        case "ItemQuantitySize":
                            itemQuantitySize = reader.getElementText();
                            break;
                        case "QuantityPerApplication":
                            quantityUomId = reader.getAttributeValue(null, "UOM");
                            quantityPerApplication = reader.getElementText();
                            break;
                        case "ItemEffectiveDate":
                            itemEffectiveDate = reader.getElementText();
                            break;
                        case "AvailableDate":
                            availableDate = reader.getElementText();
                            break;
                        case "MinimumOrderQuantity":
                            minimumOrderQuantity = reader.getElementText();
                            break;
                        case "AAIAProductCategoryCode":
                            AAIAProductCategoryCode = reader.getElementText();
                            break;
                        case "PartTerminologyID":
                            partTerminologyID = reader.getElementText();
                            break;
                        case "Description":
                            // Process the Descriptions
                            descriptionsData.add(parseDescription(reader));
                            break;
                        case "ExtendedProductInformation":
                            // Process the Extended Information
                            extendedInformationData.add(parseExtendedInformation(reader));
                            break;
                        case "ProductAttribute":
                            // Process the Product Attributes
                            productAttributesData.add(parseProductAttribute(reader));
                            break;
                        case "Package":
                            // Parse the Package data
                            packageData = parseProductPackage(reader);
                            break;
                        case "PartInterchange":
                            // Process Product PartInterchange Data
                            partInterchangeData.add(parseProductPartInterchange(reader));
                            break;
                        case "DigitalFileInformation":
                            // Process Digital FileInformation
                            digitalAssetsInformation.add(parseProductDigitalInformation(reader));
                            break;
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("Item")) {
                    break; // Exit the loop when </Item> is encountered
                }
            }

            // Updated Product Data
            Map<String, Object> productData = new HashMap<>();
            productData.put("brandName", brandLabel);
            productData.put("quantityIncluded", itemQuantitySize);
            productData.put("shippingHeight", packageData.get("Height"));
            productData.put("shippingWidth", packageData.get("Width"));
            productData.put("shippingWeight", packageData.get("Weight"));
            productData.put("shippingDepth",packageData.get("Length"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date parsedDate = dateFormat.parse(availableDate);

            // Convert the Date object to java.sql.Timestamp
            Timestamp timestamp = new Timestamp(parsedDate.getTime());

            productData.put("releaseDate", timestamp);
            productData.put("productTypeId", "MARKETING_PKG_AUTO");
            productData.put("userLogin", userLogin);
            productData.put("productId", partNumber);

            if (quantityUomId.equals("EA")) {
                productData.put("quantityUomId", "OTH_ea");
            }

            dispatcher.runSync("updateProduct", productData);

            //check brandAAIAID present or not with prroductId
            GenericValue productAttribute = EntityQuery.use(delegator).from("ProductAttribute")
                    .where("productId",partNumber,"attrName","BrandAAIAID").cache().queryOne();

            if(productAttribute == null) {
                // save branAAIAID in Product Attribute
                dispatcher.runSync("createProductAttribute", UtilMisc.toMap("productId", partNumber, "attrName",
                        "BrandAAIAID", "attrValue", brandAAIAID, "userLogin", userLogin));
            }
            // Check the GTIN code for Item is present or not
            GenericValue goodIdentificatioForItem = EntityQuery.use(delegator).from("GoodIdentification")
                    .where("productId", partNumber, "goodIdentificationTypeId", "UPCA").cache().queryOne();
            if (goodIdentificatioForItem == null) {
                dispatcher.runSync("createGoodIdentification", UtilMisc.toMap("productId", partNumber,
                        "goodIdentificationTypeId", "UPCA", "idValue", itemLevelGTIN, "userLogin", userLogin));
            } else {
                dispatcher.runSync("updateGoodIdentification", UtilMisc.toMap("productId", partNumber,
                        "goodIdentificationTypeId", "UPCA", "idValue", itemLevelGTIN, "userLogin", userLogin));
            }

            // check the GTIN code for Package is present or not
            GenericValue goodIdentificatioForPackage = EntityQuery.use(delegator).from("GoodIdentification")
                    .where("productId", partNumber, "goodIdentificationTypeId", "GTIN-14").cache().queryOne();
            if (goodIdentificatioForPackage == null) {
                dispatcher.runSync("createGoodIdentification",
                        UtilMisc.toMap("productId", partNumber, "goodIdentificationTypeId", "GTIN-14", "idValue",
                                packageData.get("PackageLevelGSTIN"), "userLogin", userLogin));
            } else {
                dispatcher.runSync("updateGoodIdentification",
                        UtilMisc.toMap("productId", partNumber, "goodIdentificationTypeId", "GTIN-14", "idValue",
                                packageData.get("PackageLevelGSTIN"), "userLogin", userLogin));
            }

            if (hazardousMaterialCode.equals("Y")) {
                Map<String, Object> productFeatureData = dispatcher.runSync("createProductFeature", UtilMisc.toMap(
                        "productFeatureTypeId", "HAZMAT", "description", "Hazardous Material", "userLogin", userLogin));
                dispatcher.runSync("applyFeatureToProduct",
                        UtilMisc.toMap("productId", partNumber, "productFeatureId",
                                productFeatureData.get("productFeatureId"), "productFeatureApplTypeId",
                                "STANDARD_FEATURE", "userLogin", userLogin));
            }

            //process Descriptions
            processDescriptions(descriptionsData,partNumber);

            //process Extended Informations
            processExtendedInformations(extendedInformationData,partNumber);

            //process Product Attributes
            processProductsAttributes(productAttributesData,partNumber);

            //process partInterChangeData
            processProductsPartInterchange(partInterchangeData,partNumber);

            //process products Digital Assets Informations
            processProductsDigitalInformations(digitalAssetsInformation,partNumber);

        } catch (XMLStreamException exception) {
            System.out.println(exception);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    // Process the Description of each Item
    private static Map<String,Object> parseDescription(XMLStreamReader reader) {
        Map<String,Object> descriptionData = new HashMap<>();
        try {
            String descriptionCode = reader.getAttributeValue(null, "DescriptionCode");
            String languageCode = reader.getAttributeValue(null, "LanguageCode");
            String maintenanceType = reader.getAttributeValue(null, "MaintenanceType");
            String descriptionText = reader.getElementText();
            descriptionData.put("descriptionCode",descriptionCode);
            descriptionData.put("languageCode",languageCode);
            descriptionData.put("descriptionText",descriptionText);
        } catch (XMLStreamException exception) {
            System.out.println(exception);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        return descriptionData;
    }

    private static void processDescriptions(List<Map<String,Object>> descriptionsData,String partsNumber){
        for(Map<String,Object> descriptionData : descriptionsData){
            Object descriptionCode = descriptionData.get("descriptionCode");
            Object languageCode = descriptionData.get("languageCode");
            Object descriptionText = descriptionData.get("descriptionText");
            try{
                boolean descriptionIsPresent = false;

                //Entity condition for find the contentId's associate with product
                EntityCondition condition = EntityCondition.makeCondition(
                        EntityOperator.AND,
                        EntityCondition.makeCondition("productId", partsNumber),
                        EntityCondition.makeCondition("productContentTypeId", "LONG_DESCRIPTION"));

                // fetch all the productContent using productId and productContentTypeId
                List<GenericValue> productContents = EntityQuery.use(delegator).from("ProductContent").where(condition)
                        .cache().queryList();

                //Search the contentId using descriptionCode
                for (GenericValue productContent : productContents) {
                    Object contentId = productContent.get("contentId");
                    GenericValue descriptionContent = EntityQuery.use(delegator).from("Content")
                            .where("contentId", contentId, "contentName", descriptionCode).cache().queryOne();

                    //update decription
                    if (descriptionContent != null) {
                        Object dataResourceId = descriptionContent.get("dataResourceId");
                        dispatcher.runSync("updateElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId,
                                "textData", descriptionText, "userLogin", userLogin));
                        descriptionIsPresent = true;
                        break;
                    }
                }

                //create description
                if (!descriptionIsPresent) {
                    Map<String, Object> dataResource = dispatcher.runSync("createDataResource",
                            UtilMisc.toMap("userLogin", userLogin));
                    Object dataResourceId = dataResource.get("dataResourceId");
                    dispatcher.runSync("createElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId, "textData",
                            descriptionText, "userLogin", userLogin));
                    Map<String, Object> content = dispatcher.runSync("createContent",
                            UtilMisc.toMap("dataResourceId", dataResourceId, "contentName", descriptionCode, "localeString",
                                    languageCode, "userLogin", userLogin));

                    //create productContent using parts number , content Id and content type Id
                    if (!partsNumber.equals("")) {
                        dispatcher.runSync("createProductContent",
                                UtilMisc.toMap("productId", partsNumber, "contentId", content.get("contentId"),
                                        "productContentTypeId", "LONG_DESCRIPTION", "userLogin", userLogin));
                    }
                }
            }  catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }

    // Process the ProductExtendedInformation
    private static Map<String,Object> parseExtendedInformation(XMLStreamReader reader) {
        Map<String,Object> productExtendedInformation = new HashMap<>();
        try {
            String expiCode = reader.getAttributeValue(null, "EXPICode");
            String extendedLanguageCode = reader.getAttributeValue(null, "LanguageCode");
            String extendedMaintenanceType = reader.getAttributeValue(null, "MaintenanceType");
            String extendedInfoText = reader.getElementText();
            productExtendedInformation.put("expiCode",expiCode);
            productExtendedInformation.put("extendedLanguageCode",extendedLanguageCode);
            productExtendedInformation.put("extendedInfoText",extendedInfoText);
        } catch (XMLStreamException exception) {
            System.out.println(exception);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        return productExtendedInformation;
    }

    private static void processExtendedInformations(List<Map<String,Object>> extendedInformationData,String partsNumber){
        for(Map<String,Object> extendedInformation : extendedInformationData){
            Object expiCode = extendedInformation.get("expiCode");
            Object extendedLanguageCode = extendedInformation.get("extendedLanguageCode");
            Object extendedInfoText = extendedInformation.get("extendedInfoText");

            boolean informationIsPresent = false;
            try{
                //create content for Extended information
                Map<String, Object> contentData = dispatcher.runSync("createContent",
                        UtilMisc.<String, Object>toMap("contentName", expiCode, "localeString", extendedLanguageCode,
                                "description", extendedInfoText, "userLogin", userLogin, "statusId", "CTNT_AVAILABLE"));
                dispatcher.runSync("createContentPurpose", UtilMisc.toMap("contentId", contentData.get("contentId"),
                        "contentPurposeTypeId", "PRODUCT_INFO", "userLogin", userLogin));
                if (!partsNumber.equals("")) {
                    dispatcher.runSync("createProductContent", UtilMisc.toMap("productId", partsNumber, "contentId",
                            contentData.get("contentId"), "productContentTypeId", "EXT_INFO", "userLogin", userLogin));
                }
            }  catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }

    private static Map<String,Object> parseProductAttribute(XMLStreamReader reader) {
        Map<String,Object> productAttributesData = new HashMap<>();
        try {
            String attributeId = reader.getAttributeValue(null, "AttributeID");
            String attributeLanguageCode = reader.getAttributeValue(null, "LanguageCode");
            String attributeMaintenance = reader.getAttributeValue(null, "MaintenanceType");
            String attributePADB = reader.getAttributeValue(null, "PADBAttribute");
            String attributeRecordNum = reader.getAttributeValue(null, "RecordNumber");
            String attributeText = reader.getElementText();

            productAttributesData.put("attributeId",attributeId);
            productAttributesData.put("attributeText",attributeText);
        } catch (XMLStreamException exception) {
            System.out.println(exception);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        return productAttributesData;
    }

    private static void processProductsAttributes(List<Map<String,Object>> productAttributesData,String partNumber){
        for(Map<String,Object> productAttributeData : productAttributesData){
            Object attributeId = productAttributeData.get("attributeId");
            Object attributeText = productAttributeData.get("attributeText");
            try{
                GenericValue productAttribute = EntityQuery.use(delegator).from("ProductAttribute")
                        .where("productId", partNumber, "attrName", attributeId).cache().queryOne();

                if (productAttribute == null) {
                    dispatcher.runSync("createProductAttribute", UtilMisc.toMap("productId", partNumber, "attrName",
                            attributeId, "attrValue", attributeText, "userLogin", userLogin));
                } else {

                    dispatcher.runSync("updateProductAttribute", UtilMisc.toMap("productId", partNumber, "attrName",
                            attributeId, "attrValue", attributeText, "userLogin", userLogin));
                }
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }

    // Parse the ProductPackage information
    private static Map<String, Object> parseProductPackage(XMLStreamReader reader) throws XMLStreamException {
        String packageMaintenanceType = reader.getAttributeValue(null, "MaintenanceType");
        String packageUom = "";
        String packagelevelGSTIN = "";
        String packageQuantitySizes = "";
        String packageBarCodeCharacters = "";
        String dimensionUom = "";
        String height = "";
        String width = "";
        String length = "";
        String weightUom = "";
        String weight = "";
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();
                switch (elementName) {
                    case "PackageUOM":
                        packageUom = reader.getElementText();
                        break;
                    case "PackageLevelGTIN":
                        packagelevelGSTIN = reader.getElementText();
                        break;
                    case "PackageBarCodeCharacters":
                        packageBarCodeCharacters = reader.getElementText();
                        break;
                    case "QuantityofEaches":
                        packageQuantitySizes = reader.getElementText();
                        break;
                    case "Dimensions":
                        dimensionUom = reader.getAttributeValue(null, "UOM");
                        while (reader.hasNext()) {
                            int dimensionEvent = reader.next();
                            if (dimensionEvent == XMLStreamConstants.START_ELEMENT) {
                                String dimensionEleName = reader.getLocalName();
                                switch (dimensionEleName) {
                                    case "Height":
                                        height = reader.getElementText();
                                        break;
                                    case "Width":
                                        width = reader.getElementText();
                                        break;
                                    case "Length":
                                        length = reader.getElementText();
                                        break;
                                }
                            } else if (dimensionEvent == XMLStreamConstants.END_ELEMENT
                                    && reader.getLocalName().equals("Dimensions")) {
                                break;
                            }
                        }
                        break;
                    case "Weights":
                        weightUom = reader.getAttributeValue(null, "UOM");
                        while (reader.hasNext()) {
                            int weightEvent = reader.next();
                            if (weightEvent == XMLStreamConstants.START_ELEMENT) {
                                String weightElement = reader.getLocalName();
                                switch (weightElement) {
                                    case "Weight":
                                        weight = reader.getElementText();
                                        break;
                                }
                            } else if (weightEvent == XMLStreamConstants.END_ELEMENT
                                    && reader.getLocalName().equals("Weights")) {
                                break;
                            }
                        }
                        break;
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("Package")) {
                break; // Exit the loop when </Package> is encountered
            }
        }
        Map<String, Object> packData = new HashMap<>();
        packData.put("PackageLevelGSTIN", packagelevelGSTIN);
        packData.put("DimensionUom", dimensionUom);
        packData.put("Height", height);
        packData.put("Width", width);
        packData.put("Length", length);
        packData.put("WeightUom", weightUom);
        packData.put("Weight", weight);
        return packData;
    }

    // Process Product Interchange Data
    private static Map<String,Object> parseProductPartInterchange(XMLStreamReader reader) {
        Map<String,Object> productInterChangeData = new HashMap<>();
        try {
            String maintenanceType = reader.getAttributeValue(null, "MaintenanceType");
            String languageCode = reader.getAttributeValue(null, "LanguageCode");
            String typeCode = "";
            String interchangePartNumber = "";
            String brandAAIAID = "";
            String brandLabel = "";
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String elementName = reader.getLocalName();
                    switch (elementName) {
                        case "TypeCode":
                            typeCode = reader.getElementText();
                            break;
                        case "PartNumber":
                            interchangePartNumber = reader.getElementText();
                            interchangePartNumber = interchangePartNumber.replaceAll("\\s+", "");
                            break;
                        case "BrandAAIAID":
                            brandAAIAID = reader.getElementText();
                            break;
                        case "BrandLabel":
                            brandLabel = reader.getElementText();
                            break;
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("PartInterchange")) {
                    break; // Exit the loop when </PartInterchange> is encountered
                }
            }

            productInterChangeData.put("brandAAIAID", brandAAIAID);
            productInterChangeData.put("brandLabel",brandLabel);
            productInterChangeData.put("interchangePartNumber",interchangePartNumber);
            productInterChangeData.put("typeCode",typeCode);
        } catch (XMLStreamException exception) {
            System.out.println(exception);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        return productInterChangeData;
    }

    private static void processProductsPartInterchange(List<Map<String,Object>> productsInterchangeData, String partNumber){
        for(Map<String,Object> productInterchange : productsInterchangeData){
            Object languageCode = productInterchange.get("languageCode");
            Object typeCode = productInterchange.get("typeCode");
            String interchangePartNumber = (String) productInterchange.get("interchangePartNumber");
            Object brandAAIAID = productInterchange.get("brandAAIAID");
            Object brandLabel = productInterchange.get("brandLabel");
            try{
                // Interchange products data
                Map<String, Object> productPartsData = new HashMap<>();
                if (!brandLabel.equals("")) {
                    productPartsData.put("brandName", brandLabel);
                }
                productPartsData.put("productId", interchangePartNumber);
                productPartsData.put("productTypeId", "SUBASSEMBLY");
                productPartsData.put("internalName", interchangePartNumber);
                productPartsData.put("userLogin", userLogin);

                Map<String, Object> productAssocData = new HashMap<>();
                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                String type = "";
                // check the type code
                if (typeCode.equals("S")) {
                    type = "PRODUCT_SUBSTITUTE";
                    productAssocData.put("productAssocTypeId", "PRODUCT_SUBSTITUTE");
                } else if (typeCode.equals("O")) {
                    type = "PRODUCT_OBSOLESCENCE";
                    productAssocData.put("productAssocTypeId", "PRODUCT_OBSOLESCENCE");
                } else if (typeCode.equals("U")) {
                    type = "PRODUCT_UPGRADE";
                    productAssocData.put("productAssocTypeId", "PRODUCT_UPGRADE");
                } else {
                    type = "PRODUCT_COMPONENT";
                    productAssocData.put("productAssocTypeId", "PRODUCT_COMPONENT");
                }

                productAssocData.put("productId", partNumber);
                productAssocData.put("productIdTo", interchangePartNumber);
                productAssocData.put("fromDate", currentTimestamp);
                productAssocData.put("userLogin", userLogin);


                if (!interchangePartNumber.trim().equals("")) {
                    GenericValue product = EntityQuery.use(delegator).from("Product")
                            .where("productId", interchangePartNumber).cache().queryOne();
                    if (product == null) {
                        // Create a interchange product
                        dispatcher.runSync("createProduct", productPartsData);
                        if (!brandAAIAID.equals("")) {
                            dispatcher.runAsync("createProductAttribute", UtilMisc.toMap("productId", interchangePartNumber,
                                    "attrName", "BRANDAAIAID", "attrValue", brandAAIAID, "userLogin", userLogin));
                        }
                        // Associate the products
                        dispatcher.runSync("createProductAssoc", productAssocData);
                    } else {
                        GenericValue productAssoc = EntityQuery.use(delegator).from("ProductAssoc")
                                .where("productId", partNumber,"productIdTo",interchangePartNumber,"productAssocTypeId",type).cache().queryOne();
                        if(productAssoc == null) {
                            // Check product is associated or not
                            dispatcher.runSync("createProductAssoc", productAssocData);
                        }
                        else{
                            dispatcher.runSync("updateProductAssoc",UtilMisc.toMap("productId",partNumber,"productIdTo",interchangePartNumber,"productAssocTypeId",type,"fromDate",productAssoc.get("fromDate"),"userLogin",userLogin));
                        }

                    }
                }
            } catch (Exception exception) {
                System.out.println(exception);
            }

        }
    }

    // Process Product DigitalFileInformation
    private static Map<String,Object> parseProductDigitalInformation(XMLStreamReader reader) {
        Map<String,Object> productDigitalInformationsData = new HashMap<>();
        try {
            String maintenanceType = reader.getAttributeValue(null, "MaintenanceType");
            String languageCode = reader.getAttributeValue(null, "LanguageCode");
            String assetType = "";
            String fileName = "";
            String resolution = "";
            String fileSize = "";
            String fileType = "";
            String assetId = "";
            String assetDimensionsUom = "";
            String assetHeight = "";
            String assetWidth = "";
            String filePath = "";
            String colorMode = "";
            String background = "";
            String representation = "";
            String URI = "";
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLStreamConstants.START_ELEMENT) {
                    String elementName = reader.getLocalName();
                    switch (elementName) {
                        case "AssetType":
                            assetType = reader.getElementText();
                            break;
                        case "FileName":
                            fileName = reader.getElementText();
                            break;
                        case "Resolution":
                            resolution = reader.getElementText();
                            break;
                        case "FileType":
                            fileType = reader.getElementText();
                            break;
                        case "AssetID":
                            assetId = reader.getElementText();
                            break;
                        case "FileSize":
                            fileSize = reader.getElementText();
                            break;
                        case "FilePath":
                            filePath = reader.getElementText();
                            break;
                        case "URI":
                            URI = reader.getElementText();
                            break;
                        case "Representation":
                            representation = reader.getElementText();
                            break;
                        case "ColorMode":
                            colorMode = reader.getElementText();
                            break;
                        case "Background":
                            background = reader.getElementText();
                            break;
                        case "AssetDimensions":
                            assetDimensionsUom = reader.getAttributeValue(null, "UOM");
                            while (reader.hasNext()) {
                                int assetEvent = reader.next();
                                if (assetEvent == XMLStreamConstants.START_ELEMENT) {
                                    String assetElementName = reader.getLocalName();
                                    switch (assetElementName) {
                                        case "AssetHeight":
                                            assetHeight = reader.getElementText();
                                            break;
                                        case "AssetWidth":
                                            assetWidth = reader.getElementText();
                                            break;
                                    }
                                } else if (assetEvent == XMLStreamConstants.END_ELEMENT
                                        && reader.getLocalName().equals("AssetDimensions")) {
                                    break;
                                }
                            }
                            break;

                    }
                } else if (event == XMLStreamConstants.END_ELEMENT
                        && reader.getLocalName().equals("DigitalFileInformation")) {
                    break;
                }
            }

            productDigitalInformationsData.put("languageCode",languageCode);
            productDigitalInformationsData.put("assetType",assetType);
            productDigitalInformationsData.put("fileName",fileName);
            productDigitalInformationsData.put("fileSize",fileSize);
            productDigitalInformationsData.put("resolution",resolution);
            productDigitalInformationsData.put("fileType",fileType);
            productDigitalInformationsData.put("assetId",assetId);
            productDigitalInformationsData.put("assetDimensionsUom",assetDimensionsUom);
            productDigitalInformationsData.put("assetHeight",assetHeight);
            productDigitalInformationsData.put("assetWidth",assetWidth);
            productDigitalInformationsData.put("filePath",filePath);
            productDigitalInformationsData.put("colorMode",colorMode);
            productDigitalInformationsData.put("background",background);
            productDigitalInformationsData.put("representation",representation);
            productDigitalInformationsData.put("URI",URI);
        } catch (XMLStreamException exception) {
            System.out.println(exception);
        } catch (Exception exception) {
            System.out.println(exception);
        }
        return productDigitalInformationsData;
    }

    private static void processProductsDigitalInformations(List<Map<String,Object>> digitalAssetsData, String partsNumber){
        for(Map<String,Object> digitalAssetData : digitalAssetsData){
            String languageCode = (String) digitalAssetData.get("languageCode");
            Object assetType = digitalAssetData.get("assetType");
            Object fileName = digitalAssetData.get("fileName");
            Object resolution = digitalAssetData.get("resolution");
            Object fileSize = digitalAssetData.get("fileSize");
            Object fileType = digitalAssetData.get("fileType");
            Object assetId = digitalAssetData.get("assetId");
            Object assetDimensionsUom = digitalAssetData.get("assetDimensionsUom");
            Object assetHeight = digitalAssetData.get("assetHeight");
            Object assetWidth = digitalAssetData.get("assetWidth");
            Object filePath = digitalAssetData.get("filePath");
            Object colorMode = digitalAssetData.get("colorMode");
            Object background = digitalAssetData.get("background");
            Object representation = digitalAssetData.get("representation");
            Object URI = digitalAssetData.get("URI");

            try{

                Map<String, Object> dataResourceData = new HashMap<>();
                Map<String, Object> contentData = new HashMap<>();
                String contentType = "";
                boolean digitalAssetIsPresent = false;

                //check the file type for Product Content type
                if (fileType.equals("JPG")) {
                    contentType = "DETAIL_IMAGE_URL";
                } else {
                    contentType = "DIGITAL_DOWNLOAD";
                }

                //Entity condition for find the contentId's associate with product
                EntityCondition condition = EntityCondition.makeCondition(
                        EntityOperator.AND,
                        EntityCondition.makeCondition("productId", partsNumber),
                        EntityCondition.makeCondition("productContentTypeId", contentType));

                // fetch all the productContent using productId and productContentTypeId
                List<GenericValue> productContents = EntityQuery.use(delegator).from("ProductContent").where(condition)
                        .cache().queryList();

                //Search the contentId using fileName
                for (GenericValue productContent : productContents) {
                    Object contentId = productContent.get("contentId");
                    GenericValue content = EntityQuery.use(delegator).from("Content")
                            .where("contentId", contentId, "contentName", fileName).cache().queryOne();

                    //update the digital File Information
                    if (content != null) {
                        if(!assetType.equals("")) {
                            dispatcher.runSync("updateContent", UtilMisc.toMap("contentId", content.get("contentId"), "description",assetType,"userLogin",userLogin));
                        }
                        Object dataResourceId = content.get("dataResourceId");
                        if (URI.equals("")) {
                            dispatcher.runSync("updateDataResource", UtilMisc.toMap("dataResourceId", dataResourceId,
                                    "objectInfo", URI, "userLogin", userLogin));
                        }

                        // check the assets resolution is present or not
                        if (!resolution.equals("")) {
                            dispatcher.runSync("updateDataResourceAttribute",
                                    UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "Resolution", "attrValue",
                                            resolution, "userLogin", userLogin));
                        }

                        // check the assets colorMode is present or not
                        if (!colorMode.equals("")) {
                            dispatcher.runSync("updateDataResourceAttribute",
                                    UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "ColorMode", "attrValue",
                                            colorMode, "userLogin", userLogin));
                        }

                        if (!representation.equals("")) {
                            dispatcher.runSync("updateDataResourceAttribute",
                                    UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "Representation",
                                            "attrValue", representation, "userLogin", userLogin));
                        }

                        if (!background.equals("")) {
                            dispatcher.runSync("updateDataResourceAttribute",
                                    UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "Background", "attrValue",
                                            background, "userLogin", userLogin));
                        }
                        if (!fileSize.equals("")) {
                            dispatcher.runSync("updateDataResourceAttribute", UtilMisc.toMap("dataResourceId",
                                    dataResourceId, "attrName", "FileSize", "attrValue", fileSize, "userLogin", userLogin));
                        }

                        if (!assetHeight.equals("")) {
                            dispatcher.runSync("updateDataResourceAttribute",
                                    UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetHeight", "attrValue",
                                            assetHeight, "description", assetDimensionsUom, "userLogin", userLogin));
                        }

                        if (!assetWidth.equals("")) {
                            dispatcher.runSync("updateDataResourceAttribute",
                                    UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetWidth", "attrValue",
                                            assetWidth, "description", assetDimensionsUom, "userLogin", userLogin));
                        }

                        digitalAssetIsPresent = true;
                        break;
                    }
                }

                //create the digital file information
                if (!digitalAssetIsPresent) {
                    if (!URI.equals("")) {
                        dataResourceData.put("objectInfo", URI);
                    }

                    if (!fileType.equals("JPG")) {
                        dataResourceData.put("mimeTypeId", "image/jpeg");
                    } else if (!fileType.equals("MP4")) {
                        dataResourceData.put("mimeTypeId", "video/mp4");
                    } else {
                        dataResourceData.put("mimeTypeId", "application/pdf");
                    }

                    dataResourceData.put("userLogin", userLogin);

                    Map<String, Object> dataResource = dispatcher.runSync("createDataResource", dataResourceData);
                    Object dataResourceId = dataResource.get("dataResourceId");

                    // check the assets resolution is present or not
                    if (!resolution.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute", UtilMisc.toMap("dataResourceId", dataResourceId,
                                "attrName", "Resolution", "attrValue", resolution, "userLogin", userLogin));
                    }

                    // check the assets colorMode is present or not
                    if (!colorMode.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute", UtilMisc.toMap("dataResourceId", dataResourceId,
                                "attrName", "ColorMode", "attrValue", colorMode, "userLogin", userLogin));
                    }

                    if (!representation.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute", UtilMisc.toMap("dataResourceId", dataResourceId,
                                "attrName", "Representation", "attrValue", representation, "userLogin", userLogin));
                    }

                    if (!background.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute", UtilMisc.toMap("dataResourceId", dataResourceId,
                                "attrName", "Background", "attrValue", background, "userLogin", userLogin));
                    }
                    if (!fileSize.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute", UtilMisc.toMap("dataResourceId", dataResourceId,
                                "attrName", "FileSize", "attrValue", fileSize, "userLogin", userLogin));
                    }

                    if (!assetHeight.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetHeight", "attrValue",
                                        assetHeight, "description", assetDimensionsUom, "userLogin", userLogin));
                    }

                    if (!assetWidth.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetWidth", "attrValue",
                                        assetWidth, "description", assetDimensionsUom, "userLogin", userLogin));
                    }

                    if (languageCode != null && !languageCode.isEmpty()) {
                        contentData.put("localeString", languageCode);
                    }
                    if (!assetType.equals("")) {
                        contentData.put("description", assetType);
                    }
                    if (!fileName.equals("")) {
                        contentData.put("contentName", fileName);
                    }

                    contentData.put("dataResourceId", dataResourceId);
                    contentData.put("userLogin", userLogin);

                    Map<String, Object> contentResult = dispatcher.runSync("createContent", contentData);
                    Object contentId = contentResult.get("contentId");

                    // Create the ProductContent using partNumber and ContentId
                    if (!partsNumber.equals("")) {
                        if (fileType.equals("JPG")) {
                            dispatcher.runSync("createProductContent", UtilMisc.toMap("productId", partsNumber, "contentId",
                                    contentId, "productContentTypeId", "DETAIL_IMAGE_URL", "userLogin", userLogin));
                        } else {
                            dispatcher.runSync("createProductContent", UtilMisc.toMap("productId", partsNumber, "contentId",
                                    contentId, "productContentTypeId", "DIGITAL_DOWNLOAD", "userLogin", userLogin));
                        }
                    }
                }
            }  catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }
}
