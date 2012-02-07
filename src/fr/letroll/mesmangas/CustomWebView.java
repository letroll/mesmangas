package fr.letroll.mesmangas;

import java.io.File;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import fr.letroll.framework.Tab;

public class CustomWebView extends WebView {

    private int zoom, pageEnCour;// ,nbpages;
    private String dir;
    private String[] list;
    private Boolean swype = false;
    private File monDir;

    public void setSwype(Boolean swype) {
        this.swype = swype;
    }

    private float oldX;

    // indicate if horizontal scrollbar can't go more to the left
    private boolean overScrollLeft = false;

    // indicate if horizontal scrollbar can't go more to the right
    private boolean overScrollRight = false;

    // indicate if horizontal scrollbar can't go more to the left OR right
    private boolean isScrolling = false;

    public CustomWebView(Context context) {
        super(context);
    }

    public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // width of the vertical scrollbar
        int scrollBarWidth = getVerticalScrollbarWidth();

        // width of the view depending of you set in the layout
        int viewWidth = computeHorizontalScrollExtent();

        // width of the webpage depending of the zoom
        int innerWidth = computeHorizontalScrollRange();

        // position of the left side of the horizontal scrollbar
        int scrollBarLeftPos = computeHorizontalScrollOffset();

        // position of the right side of the horizontal scrollbar, the width of
        // scroll is the width of view minus the width of vertical scrollbar
        int scrollBarRightPos = scrollBarLeftPos + viewWidth - scrollBarWidth;

        // if left pos of scroll bar is 0 left over scrolling is true
        if (scrollBarLeftPos == 0) {
            overScrollLeft = true;
        } else {
            overScrollLeft = false;
        }

        // if right pos of scroll bar is superior to webpage width: right over
        // scrolling is true
        if (scrollBarRightPos >= innerWidth) {
            overScrollRight = true;
        } else {
            overScrollRight = false;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: // when user touch the screen
            // if scrollbar is the most left or right
            if (overScrollLeft || overScrollRight) {
                isScrolling = false;
            } else {
                isScrolling = true;
            }
            oldX = event.getX();
            break;

        // Notification.log("pageencour",""+pageEnCour);
        // Notification.log("taille list",""+(list.length-1));
        case MotionEvent.ACTION_UP: // when user stop to touch the screen
            // if scrollbar can't go more to the left OR right
            // this allow to force the user to do another gesture when he reach
            // a side
            if (!isScrolling) {
                if (event.getX() > oldX && overScrollLeft && swype) {
                    loadPreviousPage();
                }

                if (event.getX() < oldX && overScrollRight && swype) {
                    loadNextPage();
                }
            }

            break;
        default:
            break;
        }
        return super.onTouchEvent(event);
    }

    public void affichage(String page, int zoom) {
        if (page.length() < 2) page = "0" + page;
        final String URI_PREFIX = "file:///mnt/";
        String html = new String();
        File lapage = new File(dir, page);
        // Log.v("affichage",URI_PREFIX + dir + page);
        if (lapage.length() > 1) {
            html = ("<html><body><img src=\"" + URI_PREFIX + dir + "/" + page + "\" align=left></body></html>");
        } else {
            html = ("<html><body><br /><br /><br /><br /><br /><br /><h1>"+getResources().getString(R.string.imagenondisponible)+"</h1></body></html>");// +
            // getString(R.string.imagenondisponible)
            // +
            // "</h1></body></html>");
        }
        // monUtilitaire.LOG("html", html);
        setInitialScale(zoom);
        loadDataWithBaseURL(URI_PREFIX, html, "text/html", "utf-8", "");
    }

    public void init(String manga, String chapitre, int _nbpages) {
        zoom = 0;
        pageEnCour = 0;
        // nbpages = _nbpages;
        dir = "sdcard/.mesmangas/" + manga + "/" + chapitre;
        monDir = new File(dir);
        list = monDir.list();
        if (list.length != 0) affichage(list[list.length - 1], zoom);
    }

    public Boolean loadNextPage() {
        if (monDir == null || !monDir.exists()) monDir = new File(dir);
        if (monDir.list() != null) {
            list = monDir.list();
            list = Tab.reverse(list);
            if (pageEnCour < list.length - 1) {
                pageEnCour++;
                zoom = (int) (100 * getScale());
                affichage(list[pageEnCour], zoom);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Boolean loadPreviousPage() {
        if (monDir == null) monDir = new File(dir);
        if (monDir.list() != null) {
            list = monDir.list();
            list = Tab.reverse(list);
            if (pageEnCour > 0) {
                pageEnCour--;
                zoom = (int) (100 * getScale());
                affichage(list[pageEnCour], zoom);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}