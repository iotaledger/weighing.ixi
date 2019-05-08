package org.iota.ict.ixi.util;

import org.iota.ict.Ict;
import org.iota.ict.eee.call.EEEFunctionCallerImplementation;
import org.iota.ict.ixi.WeighingModule;
import org.iota.ict.utils.properties.EditableProperties;
import org.junit.After;
import org.junit.Before;

import java.util.Set;

public abstract class TestTemplate {

    protected Ict ict1;
    protected Ict ict2;
    protected EEEFunctionCallerImplementation caller;
    protected WeighingModule weighingModule;

    @Before
    public void setup() {
        EditableProperties properties1 = new EditableProperties().host("localhost").port(1337).minForwardDelay(0).maxForwardDelay(10).guiEnabled(false);
        EditableProperties properties2 = new EditableProperties().host("localhost").port(1338).minForwardDelay(0).maxForwardDelay(10).guiEnabled(false);
        ict1 = new Ict(properties1.toFinal());
        ict1.getModuleHolder().initAllModules();
        ict1.getModuleHolder().startAllModules();
        weighingModule = new WeighingModule(ict1);
        caller = new EEEFunctionCallerImplementation(ict1);
        ict2 = new Ict(properties2.toFinal());
        addNeighborToIct(ict1,ict2);
        addNeighborToIct(ict2,ict1);
    }

    @After
    public void tearDown() {
        ict1.terminate();
        ict2.terminate();
    }

    private static void addNeighborToIct(Ict ict, Ict neighbor) {
        EditableProperties properties = ict.getProperties().toEditable();
        Set<String> neighbors = properties.neighbors();
        neighbors.add(neighbor.getAddress().getHostName() + ":" + neighbor.getAddress().getPort());
        properties.neighbors(neighbors);
        ict.updateProperties(properties.toFinal());
    }

}
