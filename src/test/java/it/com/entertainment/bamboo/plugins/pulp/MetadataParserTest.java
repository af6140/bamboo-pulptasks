package it.com.entertainment.bamboo.plugins.pulp;

import com.entertainment.bamboo.plugins.pulp.model.puppet.Metadata;
import com.entertainment.bamboo.plugins.pulp.util.MetaDataParser;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created by dwang on 9/1/16.
 */
public class MetadataParserTest extends TestCase {
    private String metaJSON =null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        File currentDir = new File(".");
        System.out.println(currentDir.getAbsolutePath());
        File metaJSON= new File("./src/test/resources/metadata.json");
        this.metaJSON = FileUtils.readFileToString(metaJSON);

    }

    public void testParse() throws Exception {
        Metadata metaData= MetaDataParser.parseJsonString(this.metaJSON);
        assert(metaData.getName().getOwner().equalsIgnoreCase("af6140"));
        assert(metaData.getName().getName().equalsIgnoreCase("dummytest"));
    }
}
