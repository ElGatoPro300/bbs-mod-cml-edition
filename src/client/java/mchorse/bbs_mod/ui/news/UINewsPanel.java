package mchorse.bbs_mod.ui.news;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.dashboard.UIDashboard;
import mchorse.bbs_mod.ui.dashboard.panels.UISidebarDashboardPanel;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.UIScrollView;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs_mod.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs_mod.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs_mod.ui.framework.elements.utils.UILabel;
import mchorse.bbs_mod.ui.framework.elements.utils.UIText;
import mchorse.bbs_mod.ui.utils.UI;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.colors.Colors;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.client.MinecraftClient;
import mchorse.bbs_mod.graphics.texture.Texture;

public class UINewsPanel extends UISidebarDashboardPanel
{
    private final UIStringList list = new UIStringList((items) -> this.showSelected());
    private final UISearchList<String> search = new UISearchList<>(this.list);
    private final UIScrollView content = UI.scrollView(6, 6);
    private final UIIcon reload = new UIIcon(Icons.REFRESH, (b) -> this.reload());

    private final Gson gson = new Gson();
    private final Type type = new TypeToken<List<NewsEntry>>(){}.getType();
    private List<NewsEntry> entries = new ArrayList<>();

    public UINewsPanel(UIDashboard dashboard)
    {
        super(dashboard);

        UILabel title = new UILabel(UIKeys.NEWS_TITLE);
        title.color(Colors.WHITE);
        title.relative(this.editor).x(10).y(10).h(12);

        this.list.background();
        this.search.label(UIKeys.NEWS_SEARCH);
        this.search.relative(this.editor).x(10).y(26).w(220).h(1F, -36);

        this.content.relative(this.editor).x(250).y(10).w(1F, -260).h(1F, -20);

        this.editor.add(title, this.search, this.content);

        this.reload.tooltip(UIKeys.NEWS_RELOAD);
        this.iconBar.add(this.reload);
    }

    @Override
    public void requestNames()
    {
        this.reload();
    }

    private void reload()
    {
        CompletableFuture.runAsync(() ->
        {
            try
            {
                String json = null;

                try
                {
                    HttpClient client = HttpClient.newBuilder().build();
                    HttpRequest req = HttpRequest.newBuilder(URI.create("https://raw.githubusercontent.com/BBSCommunity/CML-NEWS/refs/heads/main/News/news.json"))
                        .GET()
                        .build();
                    HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                    if (resp.statusCode() == 200)
                    {
                        json = resp.body();
                    }
                }
                catch (Exception ignored) {}

                if (json == null || json.isEmpty())
                {
                    this.entries = new ArrayList<>();
                }
                else
                {
                    this.entries = gson.fromJson(json, type);
                }

                MinecraftClient.getInstance().execute(this::populate);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                MinecraftClient.getInstance().execute(this::populate);
            }
        });
    }

    private void populate()
    {
        this.list.clear();

        for (NewsEntry entry : this.entries)
        {
            this.list.add(entry.title);
        }

        this.list.sort();
        this.list.setIndex(this.entries.isEmpty() ? -1 : 0);
        this.showSelected();
    }

    private void showSelected()
    {
        this.content.removeAll();

        int index = this.list.getIndex();
        if (index < 0 || index >= this.entries.size())
        {
            UILabel empty = new UILabel(UIKeys.NEWS_EMPTY);
            empty.color(Colors.LIGHTER_GRAY);
            this.content.add(empty);
            this.content.resize();
            return;
        }

        NewsEntry entry = this.entries.get(index);

        UILabel title = new UILabel(IKey.raw(entry.title));
        title.color(Colors.WHITE);
        title.h(16);

        String metaText = entry.date;
        if (entry.tags != null && !entry.tags.isEmpty())
        {
            metaText += "  •  " + String.join(", ", entry.tags);
        }

        UILabel meta = new UILabel(IKey.raw(metaText));
        meta.color(Colors.GRAY);
        meta.h(12);

        UIText body = new UIText(IKey.raw(entry.body));
        body.color(Colors.LIGHTER_GRAY, true).padding(0, 2).lineHeight(12);

        this.content.add(UI.column(6, title, meta, body).marginTop(6));

        if (entry.images != null)
        {
            for (String url : entry.images)
            {
                Link link = Link.create(url);
                this.content.add(new UINewsImage(link));
            }
        }

        this.content.resize();
    }

    public static class NewsEntry
    {
        public String id;
        public String date;
        public String title;
        public String summary;
        public String body;
        public List<String> tags;
        public List<String> images;
    }

    public static class UINewsImage extends UIElement
    {
        private final Link link;

        public UINewsImage(Link link)
        {
            this.link = link;
            this.h(512);
        }

        @Override
        public void render(UIContext context)
        {
            super.render(context);

            Texture texture = BBSModClient.getTextures().getTexture(this.link);

            if (texture == null)
            {
                return;
            }

            float w = this.area.w;
            float h = this.area.h;
            float ar = texture.width / (float) texture.height;

            if (w / h > ar)
            {
                w = h * ar;
            }
            else
            {
                h = w / ar;
            }

            float x = this.area.x + (this.area.w - w) / 8F;
            float y = this.area.y + (this.area.h - h) / 8F;

            context.batcher.fullTexturedBox(texture, x, y, w, h);
        }
    }
}
