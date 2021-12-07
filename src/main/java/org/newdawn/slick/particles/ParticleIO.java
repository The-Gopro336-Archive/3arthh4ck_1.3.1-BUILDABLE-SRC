package org.newdawn.slick.particles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.ConfigurableEmitter;
import org.newdawn.slick.particles.ConfigurableEmitterFactory;
import org.newdawn.slick.particles.ParticleEmitter;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ParticleIO {
    public static ParticleSystem loadConfiguredSystem(String ref, Color mask) throws IOException {
        return ParticleIO.loadConfiguredSystem(ResourceLoader.getResourceAsStream(ref), null, null, mask);
    }

    public static ParticleSystem loadConfiguredSystem(String ref) throws IOException {
        return ParticleIO.loadConfiguredSystem(ResourceLoader.getResourceAsStream(ref), null, null, null);
    }

    public static ParticleSystem loadConfiguredSystem(File ref) throws IOException {
        return ParticleIO.loadConfiguredSystem(new FileInputStream(ref), null, null, null);
    }

    public static ParticleSystem loadConfiguredSystem(InputStream ref, Color mask) throws IOException {
        return ParticleIO.loadConfiguredSystem(ref, null, null, mask);
    }

    public static ParticleSystem loadConfiguredSystem(InputStream ref) throws IOException {
        return ParticleIO.loadConfiguredSystem(ref, null, null, null);
    }

    public static ParticleSystem loadConfiguredSystem(String ref, ConfigurableEmitterFactory factory) throws IOException {
        return ParticleIO.loadConfiguredSystem(ResourceLoader.getResourceAsStream(ref), factory, null, null);
    }

    public static ParticleSystem loadConfiguredSystem(File ref, ConfigurableEmitterFactory factory) throws IOException {
        return ParticleIO.loadConfiguredSystem(new FileInputStream(ref), factory, null, null);
    }

    public static ParticleSystem loadConfiguredSystem(InputStream ref, ConfigurableEmitterFactory factory) throws IOException {
        return ParticleIO.loadConfiguredSystem(ref, factory, null, null);
    }

    public static ParticleSystem loadConfiguredSystem(InputStream ref, ConfigurableEmitterFactory factory, ParticleSystem system, Color mask) throws IOException {
        if (factory == null) {
            factory = new ConfigurableEmitterFactory(){

                @Override
                public ConfigurableEmitter createEmitter(String name) {
                    return new ConfigurableEmitter(name);
                }
            };
        }
        try {
            boolean additive;
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(ref);
            Element element = document.getDocumentElement();
            if (!element.getNodeName().equals("system")) {
                throw new IOException("Not a particle system file");
            }
            if (system == null) {
                system = new ParticleSystem("org/newdawn/slick/data/particle.tga", 2000, mask);
            }
            if (additive = "true".equals(element.getAttribute("additive"))) {
                system.setBlendingMode(1);
            } else {
                system.setBlendingMode(2);
            }
            boolean points = "true".equals(element.getAttribute("points"));
            system.setUsePoints(points);
            NodeList list = element.getElementsByTagName("emitter");
            for (int i = 0; i < list.getLength(); ++i) {
                Element em = (Element)list.item(i);
                ConfigurableEmitter emitter = factory.createEmitter("new");
                ParticleIO.elementToEmitter(em, emitter);
                system.addEmitter(emitter);
            }
            system.setRemoveCompletedEmitters(false);
            return system;
        }
        catch (IOException e) {
            Log.error(e);
            throw e;
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException("Unable to load particle system config");
        }
    }

    public static void saveConfiguredSystem(File file, ParticleSystem system) throws IOException {
        ParticleIO.saveConfiguredSystem(new FileOutputStream(file), system);
    }

    public static void saveConfiguredSystem(OutputStream out, ParticleSystem system) throws IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("system");
            root.setAttribute("additive", "" + (system.getBlendingMode() == 1));
            root.setAttribute("points", "" + system.usePoints());
            document.appendChild(root);
            for (int i = 0; i < system.getEmitterCount(); ++i) {
                ParticleEmitter current = system.getEmitter(i);
                if (!(current instanceof ConfigurableEmitter)) {
                    throw new RuntimeException("Only ConfigurableEmitter instances can be stored");
                }
                Element element = ParticleIO.emitterToElement(document, (ConfigurableEmitter)current);
                root.appendChild(element);
            }
            StreamResult result = new StreamResult(new OutputStreamWriter(out, "utf-8"));
            DOMSource source = new DOMSource(document);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer xformer = factory.newTransformer();
            xformer.setOutputProperty("indent", "yes");
            xformer.transform(source, result);
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException("Unable to save configured particle system");
        }
    }

    public static ConfigurableEmitter loadEmitter(String ref) throws IOException {
        return ParticleIO.loadEmitter(ResourceLoader.getResourceAsStream(ref), null);
    }

    public static ConfigurableEmitter loadEmitter(File ref) throws IOException {
        return ParticleIO.loadEmitter(new FileInputStream(ref), null);
    }

    public static ConfigurableEmitter loadEmitter(InputStream ref) throws IOException {
        return ParticleIO.loadEmitter(ref, null);
    }

    public static ConfigurableEmitter loadEmitter(String ref, ConfigurableEmitterFactory factory) throws IOException {
        return ParticleIO.loadEmitter(ResourceLoader.getResourceAsStream(ref), factory);
    }

    public static ConfigurableEmitter loadEmitter(File ref, ConfigurableEmitterFactory factory) throws IOException {
        return ParticleIO.loadEmitter(new FileInputStream(ref), factory);
    }

    public static ConfigurableEmitter loadEmitter(InputStream ref, ConfigurableEmitterFactory factory) throws IOException {
        if (factory == null) {
            factory = new ConfigurableEmitterFactory(){

                @Override
                public ConfigurableEmitter createEmitter(String name) {
                    return new ConfigurableEmitter(name);
                }
            };
        }
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(ref);
            if (!document.getDocumentElement().getNodeName().equals("emitter")) {
                throw new IOException("Not a particle emitter file");
            }
            ConfigurableEmitter emitter = factory.createEmitter("new");
            ParticleIO.elementToEmitter(document.getDocumentElement(), emitter);
            return emitter;
        }
        catch (IOException e) {
            Log.error(e);
            throw e;
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException("Unable to load emitter");
        }
    }

    public static void saveEmitter(File file, ConfigurableEmitter emitter) throws IOException {
        ParticleIO.saveEmitter(new FileOutputStream(file), emitter);
    }

    public static void saveEmitter(OutputStream out, ConfigurableEmitter emitter) throws IOException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            document.appendChild(ParticleIO.emitterToElement(document, emitter));
            StreamResult result = new StreamResult(new OutputStreamWriter(out, "utf-8"));
            DOMSource source = new DOMSource(document);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer xformer = factory.newTransformer();
            xformer.setOutputProperty("indent", "yes");
            xformer.transform(source, result);
        }
        catch (Exception e) {
            Log.error(e);
            throw new IOException("Failed to save emitter");
        }
    }

    private static Element getFirstNamedElement(Element element, String name) {
        NodeList list = element.getElementsByTagName(name);
        if (list.getLength() == 0) {
            return null;
        }
        return (Element)list.item(0);
    }

    private static void elementToEmitter(Element element, ConfigurableEmitter emitter) {
        String useAdditive;
        String useOriented;
        emitter.name = element.getAttribute("name");
        emitter.setImageName(element.getAttribute("imageName"));
        String renderType = element.getAttribute("renderType");
        emitter.usePoints = 1;
        if (renderType.equals("quads")) {
            emitter.usePoints = 3;
        }
        if (renderType.equals("points")) {
            emitter.usePoints = 2;
        }
        if ((useOriented = element.getAttribute("useOriented")) != null) {
            emitter.useOriented = "true".equals(useOriented);
        }
        if ((useAdditive = element.getAttribute("useAdditive")) != null) {
            emitter.useAdditive = "true".equals(useAdditive);
        }
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "spawnInterval"), emitter.spawnInterval);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "spawnCount"), emitter.spawnCount);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "initialLife"), emitter.initialLife);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "initialSize"), emitter.initialSize);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "xOffset"), emitter.xOffset);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "yOffset"), emitter.yOffset);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "initialDistance"), emitter.initialDistance);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "speed"), emitter.speed);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "length"), emitter.length);
        ParticleIO.parseRangeElement(ParticleIO.getFirstNamedElement(element, "emitCount"), emitter.emitCount);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "spread"), emitter.spread);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "angularOffset"), emitter.angularOffset);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "growthFactor"), emitter.growthFactor);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "gravityFactor"), emitter.gravityFactor);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "windFactor"), emitter.windFactor);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "startAlpha"), emitter.startAlpha);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "endAlpha"), emitter.endAlpha);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "alpha"), emitter.alpha);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "size"), emitter.size);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "velocity"), emitter.velocity);
        ParticleIO.parseValueElement(ParticleIO.getFirstNamedElement(element, "scaleY"), emitter.scaleY);
        Element color = ParticleIO.getFirstNamedElement(element, "color");
        NodeList steps = color.getElementsByTagName("step");
        emitter.colors.clear();
        for (int i = 0; i < steps.getLength(); ++i) {
            Element step = (Element)steps.item(i);
            float offset = Float.parseFloat(step.getAttribute("offset"));
            float r = Float.parseFloat(step.getAttribute("r"));
            float g = Float.parseFloat(step.getAttribute("g"));
            float b = Float.parseFloat(step.getAttribute("b"));
            emitter.addColorPoint(offset, new Color(r, g, b, 1.0f));
        }
        emitter.replay();
    }

    private static Element emitterToElement(Document document, ConfigurableEmitter emitter) {
        Element root = document.createElement("emitter");
        root.setAttribute("name", emitter.name);
        root.setAttribute("imageName", emitter.imageName == null ? "" : emitter.imageName);
        root.setAttribute("useOriented", emitter.useOriented ? "true" : "false");
        root.setAttribute("useAdditive", emitter.useAdditive ? "true" : "false");
        if (emitter.usePoints == 1) {
            root.setAttribute("renderType", "inherit");
        }
        if (emitter.usePoints == 2) {
            root.setAttribute("renderType", "points");
        }
        if (emitter.usePoints == 3) {
            root.setAttribute("renderType", "quads");
        }
        root.appendChild(ParticleIO.createRangeElement(document, "spawnInterval", emitter.spawnInterval));
        root.appendChild(ParticleIO.createRangeElement(document, "spawnCount", emitter.spawnCount));
        root.appendChild(ParticleIO.createRangeElement(document, "initialLife", emitter.initialLife));
        root.appendChild(ParticleIO.createRangeElement(document, "initialSize", emitter.initialSize));
        root.appendChild(ParticleIO.createRangeElement(document, "xOffset", emitter.xOffset));
        root.appendChild(ParticleIO.createRangeElement(document, "yOffset", emitter.yOffset));
        root.appendChild(ParticleIO.createRangeElement(document, "initialDistance", emitter.initialDistance));
        root.appendChild(ParticleIO.createRangeElement(document, "speed", emitter.speed));
        root.appendChild(ParticleIO.createRangeElement(document, "length", emitter.length));
        root.appendChild(ParticleIO.createRangeElement(document, "emitCount", emitter.emitCount));
        root.appendChild(ParticleIO.createValueElement(document, "spread", emitter.spread));
        root.appendChild(ParticleIO.createValueElement(document, "angularOffset", emitter.angularOffset));
        root.appendChild(ParticleIO.createValueElement(document, "growthFactor", emitter.growthFactor));
        root.appendChild(ParticleIO.createValueElement(document, "gravityFactor", emitter.gravityFactor));
        root.appendChild(ParticleIO.createValueElement(document, "windFactor", emitter.windFactor));
        root.appendChild(ParticleIO.createValueElement(document, "startAlpha", emitter.startAlpha));
        root.appendChild(ParticleIO.createValueElement(document, "endAlpha", emitter.endAlpha));
        root.appendChild(ParticleIO.createValueElement(document, "alpha", emitter.alpha));
        root.appendChild(ParticleIO.createValueElement(document, "size", emitter.size));
        root.appendChild(ParticleIO.createValueElement(document, "velocity", emitter.velocity));
        root.appendChild(ParticleIO.createValueElement(document, "scaleY", emitter.scaleY));
        Element color = document.createElement("color");
        ArrayList list = emitter.colors;
        for (int i = 0; i < list.size(); ++i) {
            ConfigurableEmitter.ColorRecord record = (ConfigurableEmitter.ColorRecord)list.get(i);
            Element step = document.createElement("step");
            step.setAttribute("offset", "" + record.pos);
            step.setAttribute("r", "" + record.col.r);
            step.setAttribute("g", "" + record.col.g);
            step.setAttribute("b", "" + record.col.b);
            color.appendChild(step);
        }
        root.appendChild(color);
        return root;
    }

    private static Element createRangeElement(Document document, String name, ConfigurableEmitter.Range range) {
        Element element = document.createElement(name);
        element.setAttribute("min", "" + range.getMin());
        element.setAttribute("max", "" + range.getMax());
        element.setAttribute("enabled", "" + range.isEnabled());
        return element;
    }

    private static Element createValueElement(Document document, String name, ConfigurableEmitter.Value value) {
        Element element = document.createElement(name);
        if (value instanceof ConfigurableEmitter.SimpleValue) {
            element.setAttribute("type", "simple");
            element.setAttribute("value", "" + value.getValue(0.0f));
        } else if (value instanceof ConfigurableEmitter.RandomValue) {
            element.setAttribute("type", "random");
            element.setAttribute("value", "" + ((ConfigurableEmitter.RandomValue)value).getValue());
        } else if (value instanceof ConfigurableEmitter.LinearInterpolator) {
            element.setAttribute("type", "linear");
            element.setAttribute("min", "" + ((ConfigurableEmitter.LinearInterpolator)value).getMin());
            element.setAttribute("max", "" + ((ConfigurableEmitter.LinearInterpolator)value).getMax());
            element.setAttribute("active", "" + ((ConfigurableEmitter.LinearInterpolator)value).isActive());
            ArrayList curve = ((ConfigurableEmitter.LinearInterpolator)value).getCurve();
            for (int i = 0; i < curve.size(); ++i) {
                Vector2f point = (Vector2f)curve.get(i);
                Element pointElement = document.createElement("point");
                pointElement.setAttribute("x", "" + point.x);
                pointElement.setAttribute("y", "" + point.y);
                element.appendChild(pointElement);
            }
        } else {
            Log.warn("unkown value type ignored: " + value.getClass());
        }
        return element;
    }

    private static void parseRangeElement(Element element, ConfigurableEmitter.Range range) {
        if (element == null) {
            return;
        }
        range.setMin(Float.parseFloat(element.getAttribute("min")));
        range.setMax(Float.parseFloat(element.getAttribute("max")));
        range.setEnabled("true".equals(element.getAttribute("enabled")));
    }

    private static void parseValueElement(Element element, ConfigurableEmitter.Value value) {
        if (element == null) {
            return;
        }
        String type = element.getAttribute("type");
        String v = element.getAttribute("value");
        if (type == null || type.length() == 0) {
            if (value instanceof ConfigurableEmitter.SimpleValue) {
                ((ConfigurableEmitter.SimpleValue)value).setValue(Float.parseFloat(v));
            } else if (value instanceof ConfigurableEmitter.RandomValue) {
                ((ConfigurableEmitter.RandomValue)value).setValue(Float.parseFloat(v));
            } else {
                Log.warn("problems reading element, skipping: " + element);
            }
        } else if (type.equals("simple")) {
            ((ConfigurableEmitter.SimpleValue)value).setValue(Float.parseFloat(v));
        } else if (type.equals("random")) {
            ((ConfigurableEmitter.RandomValue)value).setValue(Float.parseFloat(v));
        } else if (type.equals("linear")) {
            String min = element.getAttribute("min");
            String max = element.getAttribute("max");
            String active = element.getAttribute("active");
            NodeList points = element.getElementsByTagName("point");
            ArrayList<Vector2f> curve = new ArrayList<Vector2f>();
            for (int i = 0; i < points.getLength(); ++i) {
                Element point = (Element)points.item(i);
                float x = Float.parseFloat(point.getAttribute("x"));
                float y = Float.parseFloat(point.getAttribute("y"));
                curve.add(new Vector2f(x, y));
            }
            ((ConfigurableEmitter.LinearInterpolator)value).setCurve(curve);
            ((ConfigurableEmitter.LinearInterpolator)value).setMin(Integer.parseInt(min));
            ((ConfigurableEmitter.LinearInterpolator)value).setMax(Integer.parseInt(max));
            ((ConfigurableEmitter.LinearInterpolator)value).setActive("true".equals(active));
        } else {
            Log.warn("unkown type detected: " + type);
        }
    }
}
