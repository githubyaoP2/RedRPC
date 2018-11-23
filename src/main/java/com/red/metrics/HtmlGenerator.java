package com.red.metrics;

import com.red.server.RedServer;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class HtmlGenerator {
    private final static String STYLE = "<style type=\"text/css\">\n" +
            "table.gridtable {\n" +
            "    font-family: verdana,arial,sans-serif;\n" +
            "    font-size:11px;\n" +
            "    color:#333333;\n" +
            "    border-width: 1px;\n" +
            "    border-color: #666666;\n" +
            "    border-collapse: collapse;\n" +
            "}\n" +
            "table.gridtable th {\n" +
            "    border-width: 1px;\n" +
            "    padding: 8px;\n" +
            "    border-style: solid;\n" +
            "    border-color: #666666;\n" +
            "    background-color: #dedede;\n" +
            "}\n" +
            "table.gridtable td {\n" +
            "    border-width: 1px;\n" +
            "    padding: 8px;\n" +
            "    border-style: solid;\n" +
            "    border-color: #666666;\n" +
            "    background-color: #ffffff;\n" +
            "}\n" +
            "</style>";

    private final static String HEADER = "<table class=\"gridtable\">\n" +
            "<tr>\n" +
            "    <th>NettyRPC Ability Detail</th>\n" +
            "</tr>";

    private final static String TAIL = "</table>";
    private final static String CELL_BEGIN = "<tr><td>";
    private final static String CELL_END = "</td></tr>";

    StringBuilder sb = new StringBuilder();

    public StringBuilder listAbilityDetail(boolean html) {
        Map<String, Object> map = RedServer.serviceMap;

        //ReflectionUtils utils = new ReflectionUtils();


        if (html) {
            sb.append(STYLE).append(HEADER);
        }

        Set<String> s = (Set<String>) map.keySet();
        Iterator<String> iter = s.iterator();
        String key;
        while (iter.hasNext()) {
            key = iter.next();
            try {
                if (html) {
                    sb.append(CELL_BEGIN);
                    listRpcsbDetail(Class.forName(key), html);
                    sb.append(CELL_END);
                } else {
                    listRpcsbDetail(Class.forName(key), html);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (html) {
            sb.append(TAIL);
        }

        return sb;
    }

    public void listRpcsbDetail(Class<?> c, boolean html) {
        if (!c.isInterface()) {
            return;
        } else {
            sb.append(Modifier.toString(c.getModifiers()) + " " + c.getName());
            sb.append(html ? " {<br>" : " {\n");

            boolean hasFields = false;
            Field[] fields = c.getDeclaredFields();
            if (fields.length != 0) {
                sb.append(html ? "&nbsp&nbsp//&nbspFields<br>" : "  // Fields\n");
                hasFields = true;
                for (Field field : fields) {
                    listField(field, html);
                }
            }

            sb.append(hasFields ? (html ? "<br>&nbsp&nbsp//&nbspMethods" : "\n  // Methods") : (html ? "&nbsp&nbsp//&nbspMethods" : "  // Methods"));
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {
                listMethod(method, html);
            }
            sb.append(html ? "<br>}<p>" : "\n}\n\n");
        }
    }

    private void listField(Field f, boolean html) {
        sb.append((html ? "&nbsp&nbsp" : "  ") + modifiers(f.getModifiers()) +
                getType(f.getType()) + " " +
                f.getName() + (html ? ";<br>" : ";\n"));
    }

    public void listMethod(Executable member, boolean html) {
        sb.append(html ? "<br>&nbsp&nbsp" : "\n  " + modifiers(member.getModifiers() & (~Modifier.FINAL)));
        if (member instanceof Method) {
            sb.append(getType(((Method) member).getReturnType()) + " ");
        }
        sb.append(member.getName() + "(");
        listTypes(member.getParameterTypes());
        sb.append(")");
        Class<?>[] exceptions = member.getExceptionTypes();
        if (exceptions.length > 0) {
            sb.append(" throws ");
        }
        listTypes(exceptions);
        sb.append(";");
    }


    private String modifiers(int m) {
        return m != 0 ? Modifier.toString(m) + " " : "";
    }

    private String getType(Class<?> t) {
        String brackets = "";
        while (t.isArray()) {
            brackets += "[]";
            t = t.getComponentType();
        }
        return t.getName() + brackets;
    }

    private void listTypes(Class<?>[] types) {
        for (int i = 0; i < types.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getType(types[i]));
        }
    }
}
