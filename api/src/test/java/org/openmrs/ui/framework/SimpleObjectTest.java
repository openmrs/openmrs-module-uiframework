package org.openmrs.ui.framework;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.ui.framework.fragment.FragmentActionUiUtils;

import java.util.Map;

public class SimpleObjectTest {

    private UiUtils ui;

    @Before
    public void before() {
        this.ui = new FragmentActionUiUtils(null, null, null);
    }

    @Test
    public void fromObject_shouldCreateSimpleObject() {
        PersonName name = new PersonName();
        name.setGivenName("John");
        name.setMiddleName("J");
        name.setFamilyName("Johnson");

        String [] properties = {"givenName", "middleName", "familyName"};
        SimpleObject simpleName = SimpleObject.fromObject(name, ui, properties);

        Assert.assertEquals("John", simpleName.get("givenName"));
        Assert.assertEquals("J", simpleName.get("middleName"));
        Assert.assertEquals("Johnson", simpleName.get("familyName"));
    }

    @Test
    public void fromObject_shouldCreateSimpleObjectWithNestedProperties() {

        Person person = new Person();
        person.setGender("M");

        PersonName name = new PersonName();
        name.setGivenName("John");
        name.setMiddleName("J");
        name.setFamilyName("Johnson");
        name.setPreferred(true);
        person.addName(name);

        String [] properties = {"gender", "personName.givenName", "personName.middleName", "personName.familyName"};
        SimpleObject simplePerson = SimpleObject.fromObject(person, ui, properties);

        Assert.assertEquals("M", simplePerson.get("gender"));
        Assert.assertEquals("John", ((Map<String,Object>) simplePerson.get("personName")).get("givenName"));
        Assert.assertEquals("J", ((Map<String,Object>) simplePerson.get("personName")).get("middleName"));
        Assert.assertEquals("Johnson", ((Map<String,Object>) simplePerson.get("personName")).get("familyName"));
    }

    @Test
    public void fromObject_shouldNotFailIfParentNodeIsNull() {
        Person person = new Person();
        person.setGender("M");

        // we try to fetch names, even though there is no person name associated with this person
        String [] properties = {"gender", "personName.givenName", "personName.middleName", "personName.familyName"};
        SimpleObject simplePerson = SimpleObject.fromObject(person, ui, properties);

        Assert.assertEquals("M", simplePerson.get("gender"));
    }
}
