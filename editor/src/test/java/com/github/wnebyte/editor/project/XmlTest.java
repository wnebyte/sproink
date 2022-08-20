package com.github.wnebyte.editor.project;

import java.io.FileReader;
import javax.xml.bind.JAXBContext;
import org.junit.Assert;
import org.junit.Test;

public class XmlTest {

    @Test
    public void testUnmarshall00() throws Exception {
        String path = this.getClass().getResource("/templates/project.xml").getPath();
        JAXBContext context = JAXBContext.newInstance(Project.class);
        Project project = (Project) context.createUnmarshaller().unmarshal(new FileReader(path));
        Assert.assertNotNull(project);
        Assert.assertNotNull(project.getEditor());
        Assert.assertNotNull(project.getEditor().getAssets());
        Assets assets = project.getEditor().getAssets();
        Assert.assertNotNull(assets);
        Assert.assertEquals(2, assets.getTabs().size());
        Assert.assertEquals(2, assets.getTabs().get(0).getSpritesheets().size());
        Assert.assertEquals(1, assets.getTabs().get(1).getSpritesheets().size());
        Assert.assertEquals("MyProject", project.getName());
        Assert.assertEquals("build/classes", project.getOutDir());
        Assert.assertEquals("Blocks", assets.getTabs().get(0).getName());
        Assert.assertEquals("image.png", assets.getTabs().get(0).getSpritesheets().get(0).getSrc());
        Assert.assertEquals("image1.png", assets.getTabs().get(0).getSpritesheets().get(1).getSrc());
        Assert.assertEquals("image2.png", assets.getTabs().get(1).getSpritesheets().get(0).getSrc());
    }
}
