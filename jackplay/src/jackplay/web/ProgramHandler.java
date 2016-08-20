package jackplay.web;

import com.sun.net.httpserver.HttpExchange;
import jackplay.bootstrap.PlayGround;
import jackplay.play.InfoCenter;
import jackplay.play.Jack;
import jackplay.bootstrap.Genre;


import java.util.Map;

public class ProgramHandler extends BaseHandler {
    Jack jack;
    InfoCenter infoCenter;

    public ProgramHandler(Jack jack, InfoCenter infoCenter) {
        this.jack = jack;
        this.infoCenter = infoCenter;
    }

    @SuppressWarnings("unchecked")
    public void process(HttpExchange http, String uri, Map<String, String> params) throws Exception {
        switch (getUriPath(uri)) {
            case "/program/addTrace":
                addTrace(http, params);
                break;
            case "/program/redefine":
                redefine(http, params);
                break;
            case "/program/undoClass":
                undoClass(http, params);
                break;
            case "/program/undoMethod":
                undoMethod(http, params);
                break;
            case "/program/currentProgram":
                CommonHandling.willReturnJson(http);
                CommonHandling.serveStringBody(http, 200, JSON.objectToJson(this.jack.getCurrentProgram()));
                break;
            default:
                CommonHandling.error_404(http);
        }
    }

    private void redefine(HttpExchange http, Map<String, String> params) throws Exception {
        jack.redefine(new PlayGround(params.get("longMethodName")), params.get("src"));
        CommonHandling.serveStringBody(http, 200, "Method is redefined");
    }

    private void addTrace(HttpExchange http, Map<String, String> params) throws Exception {
        jack.trace(new PlayGround(params.get("methodFullName")));
        CommonHandling.serveStringBody(http, 200, "Method trace is added");
    }

    private void undoMethod(HttpExchange http, Map<String, String> params) throws Exception {
        Genre g = Genre.valueOf(params.get("genre"));
        String methodFullName = params.get("methodFullName");
        jack.undoPlay(g, new PlayGround(methodFullName));
        CommonHandling.serveStringBody(http, 200, getGenreDescriptor(g) + " method is now undone - " + methodFullName);
    }

    private void undoClass(HttpExchange http, Map<String, String> params) throws Exception {
        Genre g = Genre.valueOf(params.get("genre"));
        String className = params.get("classFullName");
        jack.undoClass(g, className);
        CommonHandling.serveStringBody(http, 200, getGenreDescriptor(g) + " class is now undone - " + className);
    }

    private static String getGenreDescriptor(Genre genre) {
        if (genre == Genre.METHOD_TRACE) {
            return "Traced";
        } else {
            return "Redefined";
        }
    }
}