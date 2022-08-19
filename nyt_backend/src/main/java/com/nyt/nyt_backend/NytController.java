package com.nyt.nyt_backend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class NytController {
    public static final String XML_URL = "https://rss.nytimes.com/services/xml/rss/nyt/Technology.xml";
    public static final String IMAGE = "image";
    public static final String URL = "url";
    public static final String HEADER_IMAGE_URL = "headerImageURL";
    public static final String ITEM = "item";
    public static final String PUB_DATE = "pubDate";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String DC_CREATOR = "dc:creator";
    public static final String CREATOR = "creator";
    public static final String LINK = "link";
    public static final String MEDIA_CONTENT = "media:content";
    public static final String IMAGE_LINK = "imageLink";

    @CrossOrigin
    @RequestMapping
    public Map<String, Object> getNytXmlData() throws ParserConfigurationException, IOException, SAXException,
            ParseException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        URLConnection urlConnection = new URL(XML_URL).openConnection();
        urlConnection.addRequestProperty("Accept", "application/xml");
        Document document = builder.parse(urlConnection.getInputStream());

        Map<String, Object> xmlData = new HashMap<>();

        Element publishedDateElement = (Element) document.getElementsByTagName(PUB_DATE).item(0);
        if (publishedDateElement != null) {
            String publishedDate = publishedDateElement.getTextContent();
            String reformattedPublishedDate = reformatDate(publishedDate);
            xmlData.put(PUB_DATE, reformattedPublishedDate);
        }

        Element imageElement = (Element) document.getElementsByTagName(IMAGE).item(0);
        if (imageElement != null) {
            String headerImageURL = imageElement.getElementsByTagName(URL).item(0).getTextContent();
            xmlData.put(HEADER_IMAGE_URL, headerImageURL);
        }

        List<String> dateList = new Vector<>();
        Map<String, Object> dateItemMap = new HashMap<>();
        List<Map<String, Object>> itemList = new Vector<>();

        NodeList items = document.getElementsByTagName(ITEM);
        for (int i = 0; i < items.getLength(); i++) {
            Map<String, Object> itemInformation = new HashMap<>();

            Element item = (Element) items.item(i);

            String dateKey = null;

            Element pubDateElement = (Element) item.getElementsByTagName(PUB_DATE).item(0);
            if (pubDateElement != null) {
                String pubDate = pubDateElement.getTextContent();
                String reformattedPubDate = reformatDate(pubDate);
                dateKey = stringToDateFormat(pubDate);
                dateList.add(dateKey);
                itemInformation.put(PUB_DATE, reformattedPubDate);
            }

            Element titleElement = (Element) item.getElementsByTagName(TITLE).item(0);
            if (titleElement != null) {
                String title = titleElement.getTextContent();
                itemInformation.put(TITLE, title);
            }

            Element descriptionElement = (Element) item.getElementsByTagName(DESCRIPTION).item(0);
            if (descriptionElement != null) {
                String description = descriptionElement.getTextContent();
                itemInformation.put(DESCRIPTION, description);
            }

            Element creatorElement = (Element) item.getElementsByTagName(DC_CREATOR).item(0);
            if (creatorElement != null) {
                String creator = creatorElement.getTextContent();
                itemInformation.put(CREATOR, creator);
            }

            Element linkElement = (Element) item.getElementsByTagName(LINK).item(0);
            if (linkElement != null) {
                String link = linkElement.getTextContent();
                itemInformation.put(LINK, link);
            }

            Element mediaContent = (Element) item.getElementsByTagName(MEDIA_CONTENT).item(0);
            if (mediaContent != null) {
                if (mediaContent.hasAttribute(URL)) {
                    String imageURL = mediaContent.getAttribute(URL);
                    itemInformation.put(IMAGE_LINK, imageURL);
                }
            }

            if (dateKey != null) {
                dateItemMap.put(dateKey, itemInformation);
            }
        }

        Collections.sort(dateList, Collections.reverseOrder());
        for (String date : dateList) {
            Map<String, Object> item = (Map<String, Object>) dateItemMap.get(date);
            itemList.add(item);
        }
        xmlData.put(ITEM, itemList);

        return xmlData;
    }

    public String stringToDateFormat(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd-HH-mm-ss");
        String[] dateSplitted = date.split("\\s+");
        String day = dateSplitted[1];
        String month = dateSplitted[2];
        String year = dateSplitted[3];
        String time = dateSplitted[4];
        String newDateString = day + "-" + month + "-" + year + " " + time;
        String newDate = dateFormatter.format(formatter.parse(newDateString));
        return newDate;
    }

    public String reformatDate(String date) {
        String[] dateSplitted = date.split("\\s+");
        String dayOfTheWeek = dateSplitted[0];
        String day = dateSplitted[1];
        String month = dateSplitted[2];
        String year = dateSplitted[3];
        String time = dateSplitted[4];
        return dayOfTheWeek + " " + day + " " + month + " " + year + ", " + time;
    }
}
