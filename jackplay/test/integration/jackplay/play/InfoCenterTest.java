package integration.jackplay.play;

import testedapp.myapp.MyBaseClass;
import testedapp.myapp.MyClass;
import jackplay.TheatreRep;
import jackplay.bootstrap.PlayGround;
import jackplay.play.InfoCenter;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoCenterTest {
    InfoCenter infoCenter = TheatreRep.getInfoCenter();
    final Class MYBASECLASS = MyBaseClass.class;
    final Class MYCLASS = MyClass.class;
    final Class JAVA_UTIL_ARRAY_LIST = java.util.ArrayList.class;

    @Test
    public void shouldFindLoadedClasses() {
        List<Class> myclasses = infoCenter.findLoadedModifiableClass("testedapp.myapp.MyBaseClass");
        assertEquals(1, myclasses.size());
        assertEquals(MYBASECLASS, myclasses.get(0));
    }

    @Test
    public void shouldFindMatchingMethod() {
        Method myfunction1 = infoCenter.findMatchingMethod(MyBaseClass.class, new PlayGround("testedapp.myapp.MyBaseClass.test1(int,java.lang.String)"));
        assertNotNull(myfunction1);
        assertEquals("test1", myfunction1.getName());

        Method myfunction2 = infoCenter.findMatchingMethod(MyBaseClass.class, new PlayGround("testedapp.myapp.MyBaseClass.test2(java.lang.Object,java.util.List)"));
        assertNotNull(myfunction2);
        assertEquals("test2", myfunction2.getName());

        Method myfunction3 = infoCenter.findMatchingMethod(MyBaseClass.class, new PlayGround("testedapp.myapp.MyBaseClass.test3(java.lang.Object[],int[][])"));
        assertNotNull(myfunction3);
        assertEquals("test3", myfunction3.getName());
    }

    @Test
    public void shouldGiveLoadedMethods() throws Exception {
        List<Map<String, String>> loadedMethods = infoCenter.getAllLoadedMethods();

        Map<String, String> myfunction1 = new HashMap<>();
        myfunction1.put("classFullName", "testedapp.myapp.MyBaseClass");
        myfunction1.put("methodFullName", "testedapp.myapp.MyBaseClass.test1(int,java.lang.String)");
        myfunction1.put("methodLongName", "testedapp.myapp.MyBaseClass.test1");
        myfunction1.put("returnType", "java.lang.String");

        assertTrue(loadedMethods.contains(myfunction1));

        Map<String, String> clear = new HashMap<>();
        clear.put("classFullName", "java.util.ArrayList");
        clear.put("methodFullName", "java.util.ArrayList.clear()");
        clear.put("methodLongName", "java.util.ArrayList.clear");
        clear.put("returnType", "void");

        assertTrue(loadedMethods.contains(clear));
    }

    @Test
    public void shouldRecogniseExistenceOfMethodBody() {
        Method myAbstract = infoCenter.findMatchingMethod(MyBaseClass.class, new PlayGround("testedapp.myapp.MyBaseClass.myAbstract()"));
        Method myNative = infoCenter.findMatchingMethod(MyBaseClass.class, new PlayGround("testedapp.myapp.MyBaseClass.myNative()"));
        Method myfunction2 = infoCenter.findMatchingMethod(MyBaseClass.class, new PlayGround("testedapp.myapp.MyBaseClass.test2(java.lang.Object,java.util.List)"));

        assertFalse(infoCenter.hasMethodBody(myAbstract));
        assertFalse(infoCenter.hasMethodBody(myNative));
        assertTrue(infoCenter.hasMethodBody(myfunction2));
    }

    @Test
    public void shouldIgnorePrivateInnerClass() throws Exception {
        MyClass.load();
        assertEquals(0, infoCenter.findLoadedModifiableClass("testedapp.myapp.MyClass$MyPrivateInnerClass").size());
        assertEquals(0, infoCenter.findLoadedModifiableClass("testedapp.myapp.MyClass$MyPrivateStaticInnerClass").size());
    }

    @Test
    public void shouldIncludeProtectedInnerClass() throws Exception {
        MyClass.load();
        assertEquals(1, infoCenter.findLoadedModifiableClass("testedapp.myapp.MyClass$MyProtectedInnerClass").size());
        assertEquals(1, infoCenter.findLoadedModifiableClass("testedapp.myapp.MyClass$MyProtectedStaticInnerClass").size());
    }
}
