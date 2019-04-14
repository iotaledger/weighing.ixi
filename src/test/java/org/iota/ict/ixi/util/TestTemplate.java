package org.iota.ict.ixi.util;

import org.iota.ict.Ict;
import org.iota.ict.ixi.Weighing;
import org.iota.ict.utils.properties.EditableProperties;
import org.junit.After;
import org.junit.Before;

import java.util.Set;

public abstract class TestTemplate {

    protected Ict ict;
    protected Weighing weighingModule;

    @Before
    public void setup() {
        EditableProperties properties = new EditableProperties().host("localhost").port(1337).minForwardDelay(0).maxForwardDelay(10).guiEnabled(false);
        ict = new Ict(properties.toFinal());
        ict.getModuleHolder().initAllModules();
        ict.getModuleHolder().startAllModules();
        weighingModule = new Weighing(ict);
    }

    @After
    public void tearDown() {
        ict.terminate();
    }

    private static void addNeighborToIct(Ict ict, Ict neighbor) {
        EditableProperties properties = ict.getProperties().toEditable();
        Set<String> neighbors = properties.neighbors();
        neighbors.add(neighbor.getAddress().getHostName() + ":" + neighbor.getAddress().getPort());
        properties.neighbors(neighbors);
        ict.updateProperties(properties.toFinal());
    }

}
