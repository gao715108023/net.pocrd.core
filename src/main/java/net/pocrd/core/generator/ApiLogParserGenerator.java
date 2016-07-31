package net.pocrd.core.generator;

import net.pocrd.entity.CompileConfig;
import net.pocrd.util.WebRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by rendong on 16/2/17.
 */
public class ApiLogParserGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ApiLogParserGenerator.class);

    public static void generate(String website, String outputPath) {
        InputStream defaultXslt = null;
        try {
            defaultXslt = ApiSdkJavaGenerator.class.getResourceAsStream("/xslt/logparser.xslt");
            Transformer trans = TransformerFactory.newInstance().newTransformer(new StreamSource(defaultXslt));
            trans.setOutputProperty("omit-xml-declaration", "yes");
            byte[] bytes = WebRequestUtil.getResponseBytes(website, null);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(bytes));
            trans.transform(new DOMSource(document), new StreamResult(outputPath + "/AutoGeneratedParser.java"));
        } catch (Exception e) {
            logger.error("generate failed!", e);
            throw new RuntimeException("generate failed!", e);
        } finally {
            try {
                if (defaultXslt != null) {
                    defaultXslt.close();
                }
            } catch (IOException e) {
                logger.error("close failed!", e);
                throw new RuntimeException("close failed!", e);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 2 && args[0] != null && args[1] != null) {
            generate(args[0], args[1]);
        } else {
            if (CompileConfig.isDebug) {
                generate("http://115.28.160.84/info.api?raw", "/Users/rendong/Desktop");
            } else {
                System.out.println("error parameter.  args[0]:source url   args[1]:output path");
            }
        }
    }
}
