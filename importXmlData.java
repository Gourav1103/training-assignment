//Imports neccessary packages
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
import java.util.Objects;

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

            // Product Data
            Map<String, Object> productData = new HashMap<>();
            productData.put("productId", partNumber);
            productData.put("internalName", partNumber);
            productData.put("productTypeId", "AGGREGATED_CONF");
            productData.put("brandName", brandLabel);
            productData.put("quantityIncluded", itemQuantitySize);
            productData.put("shippingHeight", packageData.get("Height"));
            productData.put("shippingWidth", packageData.get("Width"));
            productData.put("shippingWeight", packageData.get("Weight"));
            productData.put("shippingDepth", packageData.get("Length"));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date parsedDate = dateFormat.parse(availableDate);

            // Convert the Date object to java.sql.Timestamp
            Timestamp timestamp = new Timestamp(parsedDate.getTime());

            productData.put("releaseDate", timestamp);
            productData.put("userLogin", userLogin);

            if (quantityUomId.equals("EA")) {
                productData.put("quantityUomId", "OTH_ea");
            }

            // process Product Data
            processProductInformation(productData);

            // process BrandAAIAID
            processBrandAAIAID(partNumber, brandAAIAID);

            // process GTINCODE for Item
            processGoodIdentification(partNumber, itemLevelGTIN, "UPCA");

            // process GTINCODE for Package
            String packageGTINCode = (String) packageData.get("PackageLevelGSTIN");
            processGoodIdentification(partNumber, packageGTINCode, "GTIN-14");

            if (hazardousMaterialCode.equals("Y")) {
                Map<String, Object> productFeatureData = dispatcher.runSync("createProductFeature", UtilMisc.toMap(
                        "productFeatureTypeId", "HAZMAT", "description", "Hazardous Material", "userLogin", userLogin));
                dispatcher.runSync("applyFeatureToProduct",
                        UtilMisc.toMap("productId", partNumber, "productFeatureId",
                                productFeatureData.get("productFeatureId"), "productFeatureApplTypeId",
                                "STANDARD_FEATURE", "userLogin", userLogin));
            }

            // process Descriptions
            processDescriptions(descriptionsData, partNumber);

            // process Extended Informations
            processExtendedInformations(extendedInformationData, partNumber);

            // process Product Attributes
            processProductsAttributes(productAttributesData, partNumber);

            // process partInterChangeData
            processProductsPartInterchange(partInterchangeData, partNumber);

            // process products Digital Assets Informations
            processProductsDigitalInformations(digitalAssetsInformation, partNumber);

        } catch (XMLStreamException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // Process Product Information
    private static void processProductInformation(Map<String, Object> productData) {
        try {
            GenericValue product = null;
            try {
                // Query the database for the product using productId
                product = EntityQuery.use(delegator).from("Product")
                        .where("productId", productData.get("productId")).cache().queryOne();
            } catch (GenericEntityException exception) {
                exception.printStackTrace();
            }
            // Create a new Product if it doesn't exist
            if (product == null) {
                dispatcher.runSync("createProduct", productData);
            } else {
                // Update the existing Product
                dispatcher.runSync("updateProduct", productData);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // Process Brand AAIAID
    private static void processBrandAAIAID(String partNumber, String brandValue) {
        try {
            GenericValue productAttribute = null;
            try {
                // Query the database for a product attribute named "BrandAAIAID" with the given
                // partNumber
                productAttribute = EntityQuery.use(delegator).from("ProductAttribute")
                        .where("productId", partNumber, "attrName", "BrandAAIAID").cache().queryOne();
            } catch (GenericEntityException exception) {
                exception.printStackTrace();
            }

            // If the product attribute "BrandAAIAID" doesn't exist, create it
            if (productAttribute == null) {
                dispatcher.runSync("createProductAttribute", UtilMisc.toMap("productId", partNumber, "attrName",
                        "BrandAAIAID", "attrValue", brandValue, "userLogin", userLogin));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // Process Good Identification
    private static void processGoodIdentification(String partNumber, String GTINCode, String GTINType) {
        try {
            GenericValue goodIdentificationForItem = null;
            try {
                // Query the database for a good identification with the given GTIN code and
                // type
                goodIdentificationForItem = EntityQuery.use(delegator).from("GoodIdentification")
                        .where("productId", partNumber, "goodIdentificationTypeId", GTINType).cache().queryOne();
            } catch (GenericEntityException exception) {
                exception.printStackTrace();
            }

            // Create or Update the Good Identification based on its presence
            if (goodIdentificationForItem == null) {
                // Create a new Good Identification
                dispatcher.runSync("createGoodIdentification", UtilMisc.toMap("productId", partNumber,
                        "goodIdentificationTypeId", GTINType, "idValue", GTINCode, "userLogin", userLogin));
            } else {
                // Update the existing Good Identification
                dispatcher.runSync("updateGoodIdentification", UtilMisc.toMap("productId", partNumber,
                        "goodIdentificationTypeId", GTINType, "idValue", GTINCode, "userLogin", userLogin));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // Parse the Description of each Item
    private static Map<String, Object> parseDescription(XMLStreamReader reader) {
        Map<String, Object> descriptionData = new HashMap<>();
        try {
            String descriptionCode = reader.getAttributeValue(null, "DescriptionCode");
            String languageCode = reader.getAttributeValue(null, "LanguageCode");
            String maintenanceType = reader.getAttributeValue(null, "MaintenanceType");
            String descriptionText = reader.getElementText();
            descriptionData.put("descriptionCode", descriptionCode);
            descriptionData.put("languageCode", languageCode);
            descriptionData.put("descriptionText", descriptionText);
        } catch (XMLStreamException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return descriptionData;
    }

    // Process Description
    private static void processDescriptions(List<Map<String, Object>> descriptionsData, String partsNumber) {
        // Iterate through each description data
        descriptionsData.stream().forEach(descriptionData -> {
            String descriptionCode = (String) descriptionData.get("descriptionCode");
            String languageCode = (String) descriptionData.get("languageCode");
            String descriptionText = (String) descriptionData.get("descriptionText");

            try {
                // Create a condition to query for product contents
                EntityCondition condition = EntityCondition.makeCondition(
                        EntityOperator.AND,
                        EntityCondition.makeCondition("productId", partsNumber),
                        EntityCondition.makeCondition("productContentTypeId", "LONG_DESCRIPTION"));

                List<GenericValue> productContents = null;
                try {
                    // Query the database for matching product contents
                    productContents = EntityQuery.use(delegator)
                            .from("ProductContent")
                            .where(condition)
                            .cache()
                            .queryList();
                } catch (GenericEntityException exception) {
                    exception.printStackTrace();
                }

                GenericValue descriptionContent = null;
                if (productContents != null) {
                    try {
                        // Find the appropriate description content by filtering and mapping
                        descriptionContent = productContents.stream()
                                .map(productContent -> productContent.get("contentId"))
                                .map(contentId -> {
                                    GenericValue content = null;
                                    try {
                                        // Query the database for content information
                                        content = EntityQuery.use(delegator)
                                                .from("Content")
                                                .where("contentId", contentId, "contentName", descriptionCode)
                                                .cache()
                                                .queryOne();
                                    } catch (GenericEntityException exception) {
                                        exception.printStackTrace();
                                    }
                                    return content;
                                })
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                if (descriptionContent != null) {
                    // If description content exists, update it with new text data
                    Object dataResourceId = descriptionContent.get("dataResourceId");
                    dispatcher.runSync("updateElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId,
                            "textData", descriptionText, "userLogin", userLogin));

                } else {
                    // If description content doesn't exist, create new data resource, electronic text, and content
                    Map<String, Object> dataResource = dispatcher.runSync("createDataResource",
                            UtilMisc.toMap("userLogin", userLogin));
                    Object dataResourceId = dataResource.get("dataResourceId");

                    dispatcher.runSync("createElectronicText",
                            UtilMisc.toMap("dataResourceId", dataResourceId, "textData",
                                    descriptionText, "userLogin", userLogin));

                    Map<String, Object> content = dispatcher.runSync("createContent",
                            UtilMisc.toMap("dataResourceId", dataResourceId, "contentName", descriptionCode,
                                    "localeString", languageCode, "userLogin", userLogin));

                    //Associate content with the product
                    dispatcher.runSync("createProductContent",
                            UtilMisc.toMap("productId", partsNumber, "contentId", content.get("contentId"),
                                    "productContentTypeId", "LONG_DESCRIPTION", "userLogin", userLogin));
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    // Parse the ProductExtendedInformation
    private static Map<String, Object> parseExtendedInformation(XMLStreamReader reader) {
        Map<String, Object> productExtendedInformation = new HashMap<>();
        try {
            String expiCode = reader.getAttributeValue(null, "EXPICode");
            String extendedLanguageCode = reader.getAttributeValue(null, "LanguageCode");
            String extendedMaintenanceType = reader.getAttributeValue(null, "MaintenanceType");
            String extendedInfoText = reader.getElementText();
            productExtendedInformation.put("expiCode", expiCode);
            productExtendedInformation.put("extendedLanguageCode", extendedLanguageCode);
            productExtendedInformation.put("extendedInfoText", extendedInfoText);
        } catch (XMLStreamException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return productExtendedInformation;
    }

    // Process Extended Information
    private static void processExtendedInformations(List<Map<String, Object>> extendedInformationData, String partsNumber) {

        // Iterate through each extended information data
        extendedInformationData.stream().forEach(extendedInformation -> {
            String expiCode = (String) extendedInformation.get("expiCode");
            String extendedLanguageCode = (String) extendedInformation.get("extendedLanguageCode");
            String extendedInfoText = (String) extendedInformation.get("extendedInfoText");

            try {
                // Create a condition to query for product contents
                EntityCondition condition = EntityCondition.makeCondition(
                        EntityOperator.AND,
                        EntityCondition.makeCondition("productId", partsNumber),
                        EntityCondition.makeCondition("productContentTypeId", "EXT_INFO"));

                List<GenericValue> productContents = null;
                // Query the database for matching product contents
                try {
                    productContents = EntityQuery.use(delegator)
                            .from("ProductContent")
                            .where(condition)
                            .cache()
                            .queryList();
                } catch (GenericEntityException exception) {
                    exception.printStackTrace();
                }

                GenericValue extendedContent = null;
                if (productContents != null) {
                    try {
                        // Find the appropriate extended content by filtering and mapping
                        extendedContent = productContents.stream()
                                .map(productContent -> productContent.get("contentId"))
                                .map(contentId -> {
                                    GenericValue content = null;
                                    try {
                                        // Query the database for content information
                                        content = EntityQuery.use(delegator)
                                                .from("Content")
                                                .where("contentId", contentId, "contentName", expiCode, "description",
                                                        extendedInfoText,
                                                        "localeString", extendedLanguageCode)
                                                .cache()
                                                .queryOne();
                                    } catch (GenericEntityException exception) {
                                        exception.printStackTrace();
                                    }
                                    return content;
                                })
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                if (extendedContent == null) {
                    // If extended content doesn't exist, create new content and associate with
                    // product
                    Map<String, Object> contentData = dispatcher.runSync("createContent",
                            UtilMisc.<String, Object>toMap("contentName", expiCode, "localeString",
                                    extendedLanguageCode,
                                    "description", extendedInfoText, "userLogin", userLogin, "statusId",
                                    "CTNT_AVAILABLE"));
                    dispatcher.runSync("createProductContent", UtilMisc.toMap("productId", partsNumber, "contentId",
                            contentData.get("contentId"), "productContentTypeId", "EXT_INFO", "userLogin",
                            userLogin));

                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    // Parse the Product Attributes
    private static Map<String, Object> parseProductAttribute(XMLStreamReader reader) {
        Map<String, Object> productAttributesData = new HashMap<>();
        try {
            String attributeId = reader.getAttributeValue(null, "AttributeID");
            String attributeLanguageCode = reader.getAttributeValue(null, "LanguageCode");
            String attributeMaintenance = reader.getAttributeValue(null, "MaintenanceType");
            String attributePADB = reader.getAttributeValue(null, "PADBAttribute");
            String attributeRecordNum = reader.getAttributeValue(null, "RecordNumber");
            String attributeText = reader.getElementText();

            productAttributesData.put("attributeId", attributeId);
            productAttributesData.put("attributeText", attributeText);
        } catch (XMLStreamException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return productAttributesData;
    }

    // Process product attributes
    private static void processProductsAttributes(List<Map<String, Object>> productAttributesData, String partNumber) {

        // Iterate through each product attribute data
        productAttributesData.stream().forEach(productAttributeData -> {
            String attributeId = (String) productAttributeData.get("attributeId");
            String attributeText = (String) productAttributeData.get("attributeText");

            try {
                GenericValue productAttribute = null;
                try {
                    // Query the database to find an existing product attribute
                    productAttribute = EntityQuery.use(delegator).from("ProductAttribute")
                            .where("productId", partNumber, "attrName", attributeId).cache().queryOne();
                } catch (GenericEntityException exception) {
                    exception.printStackTrace();
                }

                if (productAttribute == null) {
                    // If product attribute doesn't exist, create a new one
                    dispatcher.runSync("createProductAttribute", UtilMisc.toMap("productId", partNumber, "attrName",
                            attributeId, "attrValue", attributeText, "userLogin", userLogin));
                } else {
                    // If product attribute exists, update its value
                    dispatcher.runSync("updateProductAttribute", UtilMisc.toMap("productId", partNumber, "attrName",
                            attributeId, "attrValue", attributeText, "userLogin", userLogin));
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    // Parse the ProductPackage information
    private static Map<String, Object> parseProductPackage(XMLStreamReader reader) {
        Map<String, Object> packageData = new HashMap<>();
        try {
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
            packageData.put("PackageLevelGSTIN", packagelevelGSTIN);
            packageData.put("DimensionUom", dimensionUom);
            packageData.put("Height", height);
            packageData.put("Width", width);
            packageData.put("Length", length);
            packageData.put("WeightUom", weightUom);
            packageData.put("Weight", weight);
        } catch(XMLStreamException exception){
            exception.printStackTrace();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return packageData;
    }

    // Parse Product Interchange Data
    private static Map<String, Object> parseProductPartInterchange(XMLStreamReader reader) {
        Map<String, Object> productInterChangeData = new HashMap<>();
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
            productInterChangeData.put("brandLabel", brandLabel);
            productInterChangeData.put("interchangePartNumber", interchangePartNumber);
            productInterChangeData.put("typeCode", typeCode);
        } catch (XMLStreamException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return productInterChangeData;
    }

    // Process Product Interchange data
    private static void processProductsPartInterchange(List<Map<String, Object>> productsInterchangeData, String partNumber) {

        // Iterate through each product interchange data
        productsInterchangeData.stream().forEach(productInterchange -> {
            String languageCode = (String) productInterchange.get("languageCode");
            String typeCode = (String) productInterchange.get("typeCode");
            String interchangePartNumber = (String) productInterchange.get("interchangePartNumber");
            String brandAAIAID = (String) productInterchange.get("brandAAIAID");
            String brandLabel = (String) productInterchange.get("brandLabel");

            try {
                // Create product parts data for interchange part
                Map<String, Object> productPartsData = new HashMap<>();
                if (!brandLabel.equals("")) {
                    productPartsData.put("brandName", brandLabel);
                }
                productPartsData.put("productId", interchangePartNumber);
                productPartsData.put("productTypeId", "SUBASSEMBLY");
                productPartsData.put("internalName", interchangePartNumber);
                productPartsData.put("userLogin", userLogin);

                // Create product association data
                Map<String, Object> productAssocData = new HashMap<>();
                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                // Determine the type and association type based on type code
                String type = "";
                String productAssocTypeId = "";
                switch (typeCode) {
                    case "S":
                        type = "PRODUCT_SUBSTITUTE";
                        productAssocTypeId = "PRODUCT_SUBSTITUTE";
                        break;
                    case "O":
                        type = "PRODUCT_OBSOLESCENCE";
                        productAssocTypeId = "PRODUCT_OBSOLESCENCE";
                        break;
                    case "U":
                        type = "PRODUCT_UPGRADE";
                        productAssocTypeId = "PRODUCT_UPGRADE";
                        break;
                    default:
                        type = "PRODUCT_COMPONENT";
                        productAssocTypeId = "PRODUCT_COMPONENT";
                        break;
                }
                productAssocData.put("productAssocTypeId", productAssocTypeId);
                productAssocData.put("productId", partNumber);
                productAssocData.put("productIdTo", interchangePartNumber);
                productAssocData.put("fromDate", currentTimestamp);
                productAssocData.put("userLogin", userLogin);

                if (!interchangePartNumber.trim().equals("")) {
                    GenericValue product = null;
                    try {
                        // Query the database to find if interchange product already exists
                        product = EntityQuery.use(delegator).from("Product")
                                .where("productId", interchangePartNumber).cache().queryOne();
                    } catch (GenericEntityException exception) {
                        exception.printStackTrace();
                    }
                    if (product == null) {
                        // Create a new interchange product
                        dispatcher.runSync("createProduct", productPartsData);
                        if (!brandAAIAID.equals("")) {
                            processBrandAAIAID(interchangePartNumber, brandAAIAID);
                        }
                        // Associate the products
                        dispatcher.runSync("createProductAssoc", productAssocData);
                    } else {
                        GenericValue productAssoc = null;
                        try {
                            // Check if the product association already exists
                            productAssoc = EntityQuery.use(delegator).from("ProductAssoc")
                                    .where("productId", partNumber, "productIdTo", interchangePartNumber,
                                            "productAssocTypeId", type)
                                    .cache().queryOne();
                        } catch (GenericEntityException exception) {
                            exception.printStackTrace();
                        }
                        if (productAssoc == null) {
                            // Create the product association
                            dispatcher.runSync("createProductAssoc", productAssocData);
                        } else {
                            // Update the existing product association
                            dispatcher.runSync("updateProductAssoc",
                                    UtilMisc.toMap("productId", partNumber, "productIdTo", interchangePartNumber,
                                            "productAssocTypeId", type, "fromDate", productAssoc.get("fromDate"),
                                            "userLogin", userLogin));
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    // Parse Product DigitalFileInformation
    private static Map<String, Object> parseProductDigitalInformation(XMLStreamReader reader) {
        Map<String, Object> productDigitalInformationsData = new HashMap<>();
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

            productDigitalInformationsData.put("languageCode", languageCode);
            productDigitalInformationsData.put("assetType", assetType);
            productDigitalInformationsData.put("fileName", fileName);
            productDigitalInformationsData.put("fileSize", fileSize);
            productDigitalInformationsData.put("resolution", resolution);
            productDigitalInformationsData.put("fileType", fileType);
            productDigitalInformationsData.put("assetId", assetId);
            productDigitalInformationsData.put("assetDimensionsUom", assetDimensionsUom);
            productDigitalInformationsData.put("assetHeight", assetHeight);
            productDigitalInformationsData.put("assetWidth", assetWidth);
            productDigitalInformationsData.put("filePath", filePath);
            productDigitalInformationsData.put("colorMode", colorMode);
            productDigitalInformationsData.put("background", background);
            productDigitalInformationsData.put("representation", representation);
            productDigitalInformationsData.put("URI", URI);
        } catch (XMLStreamException exception) {
            exception.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return productDigitalInformationsData;
    }

    // Process Product Digital Information
    private static void processProductsDigitalInformations(List<Map<String, Object>> digitalAssetsData, String partsNumber) {

        // Iterate through each digital asset data
        digitalAssetsData.stream().forEach(digitalAssetData -> {
            String languageCode = (String) digitalAssetData.get("languageCode");
            String assetType = (String) digitalAssetData.get("assetType");
            String fileName = (String) digitalAssetData.get("fileName");
            String resolution = (String) digitalAssetData.get("resolution");
            String fileSize = (String) digitalAssetData.get("fileSize");
            String fileType = (String) digitalAssetData.get("fileType");
            String assetId = (String) digitalAssetData.get("assetId");
            String assetDimensionsUom = (String) digitalAssetData.get("assetDimensionsUom");
            String assetHeight = (String) digitalAssetData.get("assetHeight");
            String assetWidth = (String) digitalAssetData.get("assetWidth");
            String filePath = (String) digitalAssetData.get("filePath");
            String colorMode = (String) digitalAssetData.get("colorMode");
            String background = (String) digitalAssetData.get("background");
            String representation = (String) digitalAssetData.get("representation");
            String URI = (String) digitalAssetData.get("URI");
            Map<String, Object> dataResourceData = new HashMap<>();
            Map<String, Object> contentData = new HashMap<>();

            // Determine content type based on file type
            String contentType = fileType.equals("JPG") ? "DETAIL_IMAGE_URL" : "DIGITAL_DOWNLOAD";
            try {
                // Entity condition to find contentId associated with the product
                EntityCondition condition = EntityCondition.makeCondition(
                        EntityOperator.AND,
                        EntityCondition.makeCondition("productId", partsNumber),
                        EntityCondition.makeCondition("productContentTypeId", contentType));

                List<GenericValue> productContents = null;
                try {
                    // Query for product contents based on the condition
                    productContents = EntityQuery.use(delegator)
                            .from("ProductContent")
                            .where(condition)
                            .cache()
                            .queryList();
                } catch (GenericEntityException exception) {
                    exception.printStackTrace();
                }
                GenericValue digitalContent = null;
                if (productContents != null) {
                    try {
                        // Find the appropriate digital content by filtering and mapping
                        digitalContent = productContents.stream()
                                .map(productContent -> productContent.get("contentId"))
                                .map(contentId -> {
                                    GenericValue content = null;
                                    try {
                                        // Query the database for content information
                                        content = EntityQuery.use(delegator)
                                                .from("Content")
                                                .where("contentId", contentId, "contentName", fileName)
                                                .cache()
                                                .queryOne();
                                    } catch (GenericEntityException e) {
                                        e.printStackTrace();
                                    }
                                    return content;
                                })
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElse(null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                if (digitalContent != null) {
                    // Update existing digital content and associated attributes
                    if (!assetType.equals("")) {
                        dispatcher.runSync("updateContent", UtilMisc.toMap("contentId", digitalContent.get("contentId"),
                                "description", assetType, "userLogin", userLogin));
                    }
                    Object dataResourceId = digitalContent.get("dataResourceId");
                    if (!URI.equals("")) {
                        dispatcher.runSync("updateDataResource", UtilMisc.toMap("dataResourceId", dataResourceId,
                                "objectInfo", URI, "userLogin", userLogin));
                    }
                    if (!resolution.equals("")) {
                        dispatcher.runSync("updateDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "Resolution",
                                        "attrValue", resolution, "userLogin", userLogin));
                    }
                    if (!colorMode.equals("")) {
                        dispatcher.runSync("updateDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "ColorMode",
                                        "attrValue", colorMode, "userLogin", userLogin));
                    }
                    // Update additional attributes
                    if (!representation.equals("")) {
                        dispatcher.runSync("updateDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "Representation",
                                        "attrValue", representation, "userLogin", userLogin));
                    }
                    if (!background.equals("")) {
                        dispatcher.runSync("updateDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "Background",
                                        "attrValue", background, "userLogin", userLogin));
                    }
                    if (!fileSize.equals("")) {
                        dispatcher.runSync("updateDataResourceAttribute", UtilMisc.toMap("dataResourceId",
                                dataResourceId, "attrName", "FileSize", "attrValue", fileSize, "userLogin",
                                userLogin));
                    }
                    if (!assetHeight.equals("")) {
                        dispatcher.runSync("updateDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetHeight",
                                        "attrValue", assetHeight, "description", assetDimensionsUom,
                                        "userLogin", userLogin));
                    }
                    if (!assetWidth.equals("")) {
                        dispatcher.runSync("updateDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetWidth",
                                        "attrValue", assetWidth, "description", assetDimensionsUom,
                                        "userLogin", userLogin));
                    }
                } else {
                    // Create new data resource
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
                    // Create data resource attributes
                    if (!resolution.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId,
                                        "attrName", "Resolution", "attrValue", resolution,
                                        "userLogin", userLogin));
                    }
                    if (!colorMode.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId,
                                        "attrName", "ColorMode", "attrValue", colorMode,
                                        "userLogin", userLogin));
                    }
                    if (!representation.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId,
                                        "attrName", "Representation", "attrValue", representation,
                                        "userLogin", userLogin));
                    }
                    if (!background.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId,
                                        "attrName", "Background", "attrValue", background,
                                        "userLogin", userLogin));
                    }
                    if (!fileSize.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId,
                                        "attrName", "FileSize", "attrValue", fileSize,
                                        "userLogin", userLogin));
                    }
                    if (!assetHeight.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetHeight",
                                        "attrValue", assetHeight, "description", assetDimensionsUom,
                                        "userLogin", userLogin));
                    }
                    if (!assetWidth.equals("")) {
                        dispatcher.runSync("createDataResourceAttribute",
                                UtilMisc.toMap("dataResourceId", dataResourceId, "attrName", "AssetWidth",
                                        "attrValue", assetWidth, "description", assetDimensionsUom,
                                        "userLogin", userLogin));
                    }
                    // Create content data
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
                    // Determine product content type and create product content
                    if (fileType.equals("JPG")) {
                        dispatcher.runSync("createProductContent",
                                UtilMisc.toMap("productId", partsNumber, "contentId",
                                        contentId, "productContentTypeId", "DETAIL_IMAGE_URL", "userLogin",
                                        userLogin));
                    } else {
                        dispatcher.runSync("createProductContent",
                                UtilMisc.toMap("productId", partsNumber, "contentId",
                                        contentId, "productContentTypeId", "DIGITAL_DOWNLOAD", "userLogin",
                                        userLogin));
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}