/*
 * This file is part of muCommander, http://www.mucommander.com
 * Copyright (C) 2002-2010 Maxence Bernard
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.commons.ant.jnlp;

import com.mucommander.commons.ant.util.XmlWriter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Nicolas Rinaudo
 */
public class JnlpTask extends Task {
    // - Constants -----------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    /** Default value of the {@link #spec} field. */
    private static final String     DEFAULT_SPEC     = "1.0+";



    // - XML format ----------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    private static final String ELEMENT_JNLP             = "jnlp";
    private static final String ELEMENT_INFORMATION      = "information";
    private static final String ELEMENT_TITLE            = "title";
    private static final String ELEMENT_VENDOR           = "vendor";
    private static final String ELEMENT_HOMEPAGE         = "homepage";
    private static final String ELEMENT_DESCRIPTION      = "description";
    private static final String ELEMENT_ICON             = "icon";
    private static final String ELEMENT_OFFLINE_ALLOWED  = "offline-allowed";
    private static final String ELEMENT_SECURITY         = "security";
    private static final String ELEMENT_ALL_PERMISSIONS  = "all-permissions";
    private static final String ELEMENT_J2EE_PERMISSIONS = "j2ee-application-client-permissions";
    private static final String ELEMENT_RESOURCES        = "resources";
    private static final String ELEMENT_J2SE             = "j2se";
    private static final String ELEMENT_JAR              = "jar";
    private static final String ELEMENT_NATIVE_LIB       = "nativelib";
    private static final String ELEMENT_EXTENSION        = "extension";
    private static final String ELEMENT_EXT_DOWNLOAD     = "ext-download";
    private static final String ELEMENT_PROPERTY         = "property";
    private static final String ELEMENT_PACKAGE          = "package";
    private static final String ELEMENT_APPLICATION_DESC = "application-desc";
    private static final String ELEMENT_ARGUMENT         = "argument";
    private static final String ELEMENT_APPLET_DESC      = "applet-desc";
    private static final String ELEMENT_PARAM            = "param";
    private static final String ELEMENT_COMPONENT_DESC   = "component-desc";
    private static final String ELEMENT_INSTALLER_DESC   = "installer-desc";
    private static final String ATTR_SPEC                = "spec";
    private static final String ATTR_VERSION             = "version";
    private static final String ATTR_CODEBASE            = "codebase";
    private static final String ATTR_HREF                = "href";
    private static final String ATTR_LOCALE              = "locale";
    private static final String ATTR_KIND                = "kind";
    private static final String ATTR_WIDTH               = "width";
    private static final String ATTR_HEIGHT              = "height";
    private static final String ATTR_SIZE                = "size";
    private static final String ATTR_DEPTH               = "depth";
    private static final String ATTR_OS                  = "os";
    private static final String ATTR_ARCH                = "arch";
    private static final String ATTR_INITIAL_HEAP        = "initial-heap-size";
    private static final String ATTR_MAX_HEAP            = "max-heap-size";
    private static final String ATTR_MAIN                = "main";
    private static final String ATTR_DOWNLOAD            = "download";
    private static final String ATTR_PART                = "part";
    private static final String ATTR_EXT_PART            = "ext-part";
    private static final String ATTR_NAME                = "name";
    private static final String ATTR_VALUE               = "value";
    private static final String ATTR_RECURSIVE           = "recursive";
    private static final String ATTR_MAIN_CLASS          = "main-class";
    private static final String ATTR_DOCUMENT_BASE       = "documentbase";
    private static final String KIND_ONE_LINE            = "one-line";
    private static final String KIND_SHORT               = "short";
    private static final String KIND_TOOLTIP             = "tooltip";
    private static final String KIND_SELECTED            = "selected";
    private static final String KIND_DISABLED            = "disabled";
    private static final String KIND_ROLLOVER            = "rollover";
    private static final String DOWNLOAD_LAZY            = "lazy";



    // - Instance variables --------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    /** Where to store the JNLP file. */
    private File                     output;
    /** Version of the JNLP specifications used by the generated file. */
    private String                   spec;
    /** Version of the bundle described by the output file. */
    private String                   version;
    /** Root URL for all relative URLs used in the JNLP file. */
    private String                   codeBase;
    /** URL of the JNLP file. */
    private String                   href;
    /** Contains the bundle description. */
    private List<InformationElement> informations;
    /** Whether or not the bundle needs full permissions on the local machine. */
    private boolean                  allPermissions;
    /** Whether or not the bundle needs the permissions defined for a J2EE client. */
    private boolean                  j2eePermissions;
    /** resources defined for the bundle. */
    private List<ResourcesElement>   resources;
    /** Whether or not the bundle is a component. */
    private boolean                  isComponent;
    /** Contains the description of application bundles. */
    private ApplicationDescElement   applicationDesc;
    /** Contains the description of applet bundles. */
    private AppletDescElement        appletDesc;
    /** Contains the description of installer bundles. */
    private InstallerDescElement     installerDesc;



    // - Initialisation ------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Initialises the JNLP task.
     */
    public void init() {
        resources       = new ArrayList<ResourcesElement>();
        informations    = new ArrayList<InformationElement>();
        output          = null;
        spec            = DEFAULT_SPEC;
        version         = null;
        codeBase        = null;
        href            = null;
        allPermissions  = false;
        j2eePermissions = false;
        isComponent     = false;
        applicationDesc = null;
        appletDesc      = null;
        installerDesc   = null;
    }



    // - Ant interaction -----------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    public void setOut(File f) {
        output = f;
    }

    public ResourcesElement createResources() {
        ResourcesElement buffer;

        buffer = new ResourcesElement();
        resources.add(buffer);

        return buffer;
    }

    public void setComponent(boolean b) throws BuildException {
        if(b) {
            if(applicationDesc != null || appletDesc != null || installerDesc != null)
                throw new BuildException("Cannot describe multiple packages");
            isComponent = true;
        }
    }

    private void checkDescription() throws BuildException {
        if(isComponent || applicationDesc != null || installerDesc != null || appletDesc != null)
            throw new BuildException("Cannot describe multiple packages");
    }

    public ApplicationDescElement createApplicationDesc() throws BuildException {
        checkDescription();
        return applicationDesc = new ApplicationDescElement();
    }

    public AppletDescElement createAppletDesc() throws BuildException {
        checkDescription();
        return appletDesc = new AppletDescElement();
    }

    public InstallerDescElement createInstallerDesc() throws BuildException {
        checkDescription();
        return installerDesc = new InstallerDescElement();
    }

    public void setSpec(String s) {
        spec = s;
    }

    public void setVersion(String s) {
        version = s;
    }

    public void setCodeBase(String s) {
        codeBase = s;
    }

    public void setHref(String s) {
        href = s;
    }

    public void setAllPermissions(boolean b) {
        allPermissions = b;
    }

    public void setJ2EEPermissions(boolean b) {
        j2eePermissions = b;
    }

    public InformationElement createInformation() throws BuildException {
        InformationElement buffer;

        buffer = new InformationElement();
        informations.add(buffer);

        return buffer;
    }



    // - Task execution ------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    private static void addAttribute(AttributesImpl attr, String name, String value) {
        attr.addAttribute("", name, name, "string", value);
    }

    private Attributes getRootAttributes() {
        AttributesImpl attr;

        attr = new AttributesImpl();

        addAttribute(attr, ATTR_SPEC, spec);
        if(version != null)
            addAttribute(attr, ATTR_VERSION, version);
        if(codeBase != null)
            addAttribute(attr, ATTR_CODEBASE, codeBase);
        if(href != null)
            addAttribute(attr, ATTR_HREF, href);
        return attr;
    }

    private static void writeDescription(XmlWriter out, DescriptionElement description) throws SAXException {
        AttributesImpl attr;
        int            kind;

        attr = new AttributesImpl();
        if((kind = description.getKind()) != DescriptionElement.KIND_UNSPECIFIED) {
            switch(kind) {
                case DescriptionElement.KIND_ONE_LINE:
                    addAttribute(attr, ATTR_KIND, KIND_ONE_LINE);
                    break;
                case DescriptionElement.KIND_SHORT:
                    addAttribute(attr, ATTR_KIND, KIND_SHORT);
                    break;
                case DescriptionElement.KIND_TOOLTIP:
                    addAttribute(attr, ATTR_KIND, KIND_TOOLTIP);
                    break;
            }
        }
        out.startElement(ELEMENT_DESCRIPTION, attr);
        out.characters(description.getText());
        out.endElement(ELEMENT_DESCRIPTION);
    }

    private static void writeIcon(XmlWriter out, IconElement icon) throws SAXException, BuildException {
        AttributesImpl attr;

        if(icon.getHref() == null)
            throw new BuildException("Missing " + ATTR_HREF + " attribute for " + ELEMENT_ICON + " element.");

        attr = new AttributesImpl();
        switch(icon.getKind()) {
            case IconElement.KIND_SELECTED:
                addAttribute(attr, ATTR_KIND, KIND_SELECTED);
                break;
            case IconElement.KIND_ROLLOVER:
                addAttribute(attr, ATTR_KIND, KIND_ROLLOVER);
                break;
            case IconElement.KIND_DISABLED:
                addAttribute(attr, ATTR_KIND, KIND_DISABLED);
                break;
        }

        if(icon.getWidth() != 0)
            addAttribute(attr, ATTR_WIDTH, Integer.toString(icon.getWidth()));
        if(icon.getHeight() != 0)
            addAttribute(attr, ATTR_HEIGHT, Integer.toString(icon.getHeight()));
        if(icon.getDepth() != 0)
            addAttribute(attr, ATTR_DEPTH, Integer.toString(icon.getDepth()));
        if(icon.getSize() != 0)
            addAttribute(attr, ATTR_SIZE, Integer.toString(icon.getSize()));
        if(icon.getVersion() != null)
            addAttribute(attr, ATTR_VERSION, icon.getVersion());
        addAttribute(attr, ATTR_HREF, icon.getHref());
        out.addElement(ELEMENT_ICON, attr);
    }

    private static void writeInformation(XmlWriter out, InformationElement information) throws SAXException {
        AttributesImpl attr;
        Iterator<?>    iterator;

        attr = new AttributesImpl();
        if(information.getLocale() != null)
            addAttribute(attr, ATTR_LOCALE, information.getLocale());
        out.startElement(ELEMENT_INFORMATION, attr);

        if(information.getTitle() != null) {
            out.startElement(ELEMENT_TITLE);
            out.characters(information.getTitle());
            out.endElement(ELEMENT_TITLE);
        }

        if(information.getVendor() != null) {
            out.startElement(ELEMENT_VENDOR);
            out.characters(information.getVendor());
            out.endElement(ELEMENT_VENDOR);
        }

        if(information.getHomepage() != null) {
            attr = new AttributesImpl();
            addAttribute(attr, ATTR_HREF, information.getHomepage());
            out.addElement(ELEMENT_HOMEPAGE, attr);
        }

        iterator = information.descriptions();
        while(iterator.hasNext())
            writeDescription(out, (DescriptionElement)iterator.next());

        iterator = information.icons();
        while(iterator.hasNext())
            writeIcon(out, (IconElement)iterator.next());

        if(information.isOffline())
            out.addElement(ELEMENT_OFFLINE_ALLOWED);

        out.endElement(ELEMENT_INFORMATION);
    }

    private static void writeJ2se(XmlWriter out, J2seElement j2se) throws SAXException {
        AttributesImpl             attr;
        Iterator<ResourcesElement> iterator;

        if(j2se.getVersion() == null)
            throw new BuildException("Missing " + ATTR_VERSION + " attribute for " + ELEMENT_J2SE + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_VERSION, j2se.getVersion());
        if(j2se.getHref() != null)
            addAttribute(attr, ATTR_HREF, j2se.getHref());
        if(j2se.getInitialHeap() != 0)
            addAttribute(attr, ATTR_INITIAL_HEAP, Integer.toString(j2se.getInitialHeap()));
        if(j2se.getMaxHeap() != 0)
            addAttribute(attr, ATTR_MAX_HEAP, Integer.toString(j2se.getMaxHeap()));

        if(j2se.hasResources()) {
            out.startElement(ELEMENT_J2SE, attr);

            iterator = j2se.resources();
            while(iterator.hasNext())
                writeResources(out, iterator.next());

            out.endElement(ELEMENT_J2SE);
        }
        else
            out.addElement(ELEMENT_J2SE, attr);
    }

    private static void writeJar(XmlWriter out, JarElement jar) throws SAXException {
        AttributesImpl attr;

        if(jar.getHref() == null)
            throw new BuildException("Missing " + ATTR_HREF + " attribute for " + ELEMENT_JAR + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_HREF, jar.getHref());
        if(jar.getVersion() != null)
            addAttribute(attr, ATTR_VERSION, jar.getVersion());
        if(jar.getMain())
            addAttribute(attr, ATTR_MAIN, Boolean.toString(jar.getMain()));
        if(jar.getDownload() == Downloadable.DOWNLOAD_LAZY)
            addAttribute(attr, ATTR_DOWNLOAD, DOWNLOAD_LAZY);
        if(jar.getSize() != 0)
            addAttribute(attr, ATTR_SIZE, Integer.toString(jar.getSize()));
        if(jar.getPart() != null)
            addAttribute(attr, ATTR_PART, jar.getPart());

        out.addElement(ELEMENT_JAR, attr);
    }

    private static void writeNativeLib(XmlWriter out, NativeLibElement nativeLib) throws SAXException {
        AttributesImpl attr;

        if(nativeLib.getHref() == null)
            throw new BuildException("Missing " + ATTR_HREF + " attribute for " + ELEMENT_NATIVE_LIB + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_HREF, nativeLib.getHref());
        if(nativeLib.getVersion() != null)
            addAttribute(attr, ATTR_VERSION, nativeLib.getVersion());
        if(nativeLib.getDownload() == Downloadable.DOWNLOAD_LAZY)
            addAttribute(attr, ATTR_DOWNLOAD, DOWNLOAD_LAZY);
        if(nativeLib.getSize() != 0)
            addAttribute(attr, ATTR_SIZE, Integer.toString(nativeLib.getSize()));
        if(nativeLib.getPart() != null)
            addAttribute(attr, ATTR_PART, nativeLib.getPart());

        out.addElement(ELEMENT_NATIVE_LIB, attr);
    }

    private static void writeExtensionDownload(XmlWriter out, ExtDownloadElement ext) throws SAXException {
        AttributesImpl attr;

        if(ext.getExtPart() == null)
            throw new BuildException("Missing " + ATTR_EXT_PART + " attribute for " + ELEMENT_EXT_DOWNLOAD + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_EXT_PART, ext.getExtPart());
        if(ext.getPart() != null)
            addAttribute(attr, ATTR_PART, ext.getPart());
        if(ext.getDownload() == Downloadable.DOWNLOAD_LAZY)
            addAttribute(attr, ATTR_DOWNLOAD, DOWNLOAD_LAZY);

        out.addElement(ELEMENT_EXT_DOWNLOAD, attr);
    }

    private static void writeExtension(XmlWriter out, ExtensionElement extension) throws SAXException {
        AttributesImpl attr;

        if(extension.getHref() == null)
            throw new BuildException("Missing " + ATTR_HREF + " attribute for " + ELEMENT_EXTENSION + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_HREF, extension.getHref());
        if(extension.getVersion() != null)
            addAttribute(attr, ATTR_VERSION, extension.getVersion());

        if(extension.hasDownloads()) {
            Iterator<ExtDownloadElement> iterator;

            out.startElement(ELEMENT_EXTENSION, attr);

            iterator = extension.downloads();
            while(iterator.hasNext())
                writeExtensionDownload(out, iterator.next());

            out.endElement(ELEMENT_EXTENSION);
        }
        else {
            out.addElement(ELEMENT_EXTENSION, attr);
        }
    }

    private static void writeProperty(XmlWriter out, PropertyElement property, String element) throws SAXException {
        AttributesImpl attr;

        if(property.getName() == null)
            throw new BuildException("Missing " + ATTR_NAME + " attribute for " + element + " element.");
        if(property.getValue() == null)
            throw new BuildException("Missing " + ATTR_VALUE + " attribute for  " + element + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_NAME, property.getName());
        addAttribute(attr, ATTR_VALUE, property.getValue());

        out.addElement(element, attr);
    }

    private static void writePackage(XmlWriter out, PackageElement element) throws SAXException {
        AttributesImpl attr;

        if(element.getName() == null)
            throw new BuildException("Missing " + ATTR_NAME + " attribute for " + ELEMENT_PACKAGE + " element.");
        if(element.getPart() == null)
            throw new BuildException("Missing " + ATTR_PART + " attribute for " + ELEMENT_PACKAGE + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_NAME, element.getName());
        addAttribute(attr, ATTR_PART, element.getPart());
        if(element.getRecursive())
            addAttribute(attr, ATTR_RECURSIVE, Boolean.toString(element.getRecursive()));

        out.addElement(ELEMENT_PACKAGE, attr);
    }

    private static void writeResources(XmlWriter out, ResourcesElement resources) throws SAXException {
        AttributesImpl attr;
        Iterator<?>    iterator;

        attr = new AttributesImpl();
        if(resources.getOs() != null)
            addAttribute(attr, ATTR_OS, resources.getOs());
        if(resources.getArch() != null)
            addAttribute(attr, ATTR_ARCH, resources.getArch());
        if(resources.getLocale() != null)
            addAttribute(attr, ATTR_ARCH, resources.getLocale());

        out.startElement(ELEMENT_RESOURCES, attr);

        iterator = resources.j2ses();
        while(iterator.hasNext())
            writeJ2se(out, (J2seElement)iterator.next());

        iterator = resources.jars();
        while(iterator.hasNext())
            writeJar(out, (JarElement)iterator.next());

        iterator = resources.nativeLibs();
        while(iterator.hasNext())
            writeNativeLib(out, (NativeLibElement)iterator.next());

        iterator = resources.extensions();
        while(iterator.hasNext())
            writeExtension(out, (ExtensionElement)iterator.next());

        iterator = resources.properties();
        while(iterator.hasNext())
            writeProperty(out, (PropertyElement)iterator.next(), ELEMENT_PROPERTY);

        iterator = resources.packages();
        while(iterator.hasNext())
            writePackage(out, (PackageElement)iterator.next());

        out.endElement(ELEMENT_RESOURCES);
    }

    private static void writeApplicationDesc(XmlWriter out, ApplicationDescElement desc) throws SAXException {
        AttributesImpl attr;

        attr = new AttributesImpl();
        if(desc.getMain() != null)
            addAttribute(attr, ATTR_MAIN_CLASS, desc.getMain());
        if(desc.hasArguments()) {
            Iterator<ArgumentElement> iterator;

            out.startElement(ELEMENT_APPLICATION_DESC, attr);
            iterator = desc.arguments();
            while(iterator.hasNext()) {
                out.startElement(ELEMENT_ARGUMENT);
                out.characters(iterator.next().getText());
                out.endElement(ELEMENT_ARGUMENT);
            }
            out.endElement(ELEMENT_APPLICATION_DESC);
        }
        else
            out.addElement(ELEMENT_APPLICATION_DESC, attr);
    }

    private static void writeAppletDesc(XmlWriter out, AppletDescElement desc) throws SAXException {
        AttributesImpl attr;

        if(desc.getMain() == null)
            throw new BuildException("Missing " + ATTR_MAIN_CLASS + " attribute for " + ELEMENT_APPLET_DESC + " element.");
        if(desc.getName() == null)
            throw new BuildException("Missing " + ATTR_NAME + " attribute for " + ELEMENT_APPLET_DESC + " element.");
        if(desc.getWidth() == 0)
            throw new BuildException("Missing " + ATTR_WIDTH + " attribute for " + ELEMENT_APPLET_DESC + " element.");
        if(desc.getHeight() == 0)
            throw new BuildException("Missing " + ATTR_HEIGHT + " attribute for " + ELEMENT_APPLET_DESC + " element.");

        attr = new AttributesImpl();
        addAttribute(attr, ATTR_MAIN_CLASS, desc.getMain());
        addAttribute(attr, ATTR_NAME, desc.getName());
        addAttribute(attr, ATTR_WIDTH, Integer.toString(desc.getWidth()));
        addAttribute(attr, ATTR_HEIGHT, Integer.toString(desc.getHeight()));
        if(desc.getDocumentBase() != null)
            addAttribute(attr, ATTR_DOCUMENT_BASE, desc.getDocumentBase());

        if(desc.hasParams()) {
            Iterator<PropertyElement> iterator;

            out.startElement(ELEMENT_APPLET_DESC, attr);

            iterator = desc.params();
            while(iterator.hasNext())
                writeProperty(out, iterator.next(), ELEMENT_PARAM);

            out.endElement(ELEMENT_APPLET_DESC);
        }
        else
            out.addElement(ELEMENT_APPLET_DESC, attr);
    }

    private static void writeInstallerDesc(XmlWriter out, InstallerDescElement desc) throws SAXException {
        AttributesImpl attr;

        attr = new AttributesImpl();
        if(desc.getMain() != null)
            addAttribute(attr, ATTR_MAIN_CLASS, desc.getMain());
        out.addElement(ELEMENT_INSTALLER_DESC, attr);
    }

    public void execute() throws BuildException {
        XmlWriter out;
        OutputStream stream;
        Iterator<?>  iterator;

        // Makes sure everything is properly initialised.
        if(informations.isEmpty())
            throw new BuildException(ELEMENT_INFORMATION + " element not found.");
        if(output == null)
            throw new BuildException("Unspecified output file.");
        if(!isComponent && applicationDesc == null && appletDesc == null && installerDesc == null)
            throw new BuildException("Unspecified bundle type.");

        stream = null;
        try {
            // Root element.
            out = new XmlWriter(stream = new FileOutputStream(output));
            out.startDocument();
            out.startElement(ELEMENT_JNLP, getRootAttributes());

            // Information elements.
            iterator = informations.iterator();
            while(iterator.hasNext())
                writeInformation(out, (InformationElement)iterator.next());

            // Security element (if necessary).
            if(allPermissions || j2eePermissions) {
                out.startElement(ELEMENT_SECURITY);

                if(allPermissions)
                    out.addElement(ELEMENT_ALL_PERMISSIONS);
                if(j2eePermissions)
                    out.addElement(ELEMENT_J2EE_PERMISSIONS);

                out.endElement(ELEMENT_SECURITY);
            }

            // Resources elements.
            iterator = resources.iterator();
            while(iterator.hasNext())
                writeResources(out, (ResourcesElement)iterator.next());

            // Application description.
            if(applicationDesc != null)
                writeApplicationDesc(out, applicationDesc);

                // Applet description.
            else if(appletDesc != null)
                writeAppletDesc(out, appletDesc);

                // Component description.
            else if(isComponent)
                out.addElement(ELEMENT_COMPONENT_DESC);

                // Installer description.
            else
                writeInstallerDesc(out, installerDesc);

            out.endElement(ELEMENT_JNLP);
            out.endDocument();
        }
        catch(IOException e) {throw new BuildException(e);}
        catch(SAXException e) {throw new BuildException(e);}
        finally {
            if(stream != null) {
                try {stream.close();}
                catch(Exception e) {}
            }
        }
    }
}
