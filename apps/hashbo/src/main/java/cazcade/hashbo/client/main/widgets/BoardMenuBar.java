package cazcade.hashbo.client.main.widgets;

import cazcade.hashbo.client.main.menus.add.*;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * @author neilellis@cazcade.com
 */
public class BoardMenuBar extends MenuBar {

    private LiquidURI poolURI;
    private MenuBar addMenu;



    public interface SizeVariantBuilder<T extends CreateItemCommand> {

        T create(CreateItemCommand.Size size);

    }

    public BoardMenuBar() {
        super(false);
    }


    public void setUri(final LiquidURI poolURI, boolean modifierOptions) {
        this.poolURI = poolURI;
        clearItems();
        if(modifierOptions) {
        addMenu = new MenuBar(true);
            createAddMenu(poolURI);
        }

    }

    private void createAddMenu(final LiquidURI poolURI) {
        addItem(new SafeHtmlBuilder().appendHtmlConstant("<img alt=\"add\" src=\"_images/add.png\"/>").toSafeHtml(), addMenu);
        setVisible(true);
        setAnimationEnabled(true);
        setAutoOpen(true);
        setFocusOnHoverEnabled(true);

        addMenu.addItem("Sticky", new CreateRichTextCommand(poolURI, LSDDictionaryTypes.STICKY, AbstractCreateCommand.Size.DEFAULT, "default"));

        addMenu.addItem("Plain Text", createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Override
            public CreateItemCommand create(CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "default");
            }
        }));

        addMenu.addItem("White Text", createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Override
            public CreateItemCommand create(CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "white");
            }
        }));


        addMenu.addItem("Black Text", createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Override
            public CreateItemCommand create(CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "black");
            }
        }));

        addMenu.addItem("Caption", new CreateRichTextCommand(poolURI, LSDDictionaryTypes.CAPTION, AbstractCreateCommand.Size.DEFAULT, "default"));

        addMenu.addItem("Photograph",
                createMenuBarForSizeVariants(new SizeVariantBuilder() {
                    @Override
                    public CreateItemCommand create(CreateItemCommand.Size size) {
                        return new CreatePhotoCommand(poolURI, LSDDictionaryTypes.PHOTO2D, size, "default");
                    }
                }));

        addMenu.addItem("Webpage Link",
                createMenuBarForSizeVariants(new SizeVariantBuilder() {
                    @Override
                    public CreateItemCommand create(CreateItemCommand.Size size) {
                        return new CreateWebsiteCommand(poolURI, LSDDictionaryTypes.WEBPAGE, size, "default");
                    }
                }));

        addMenu.addItem("YouTube Video",
                createMenuBarForSizeVariants(new SizeVariantBuilder() {
                    @Override
                    public CreateItemCommand create(CreateItemCommand.Size size) {
                        return new CreateYouTubeCommand(poolURI, LSDDictionaryTypes.YOUTUBE_MOVIE, size, "default");
                    }
                }));

        addMenu.addItem("Decorations", createShapeMenuBar());

        if (ClientApplicationConfiguration.isAlphaFeatures()) {
            addMenu.addItem("Your Card", new CreateAliasRefCommand(poolURI, LSDDictionaryTypes.ALIAS_REF, UserUtil.getCurrentAlias().getURI()));
//                                subMenu.addItem("Custom Object (ALPHA)", new CreateCustomObjectCommand(poolURI, LSDDictionaryTypes.CUSTOM_OBJECT));
            addMenu.addItem("Checklist (ALPHA)", new CreateChecklistCommand(poolURI, LSDDictionaryTypes.CHECKLIST_POOL));
        }
    }

    private MenuBar createMenuBarForSizeVariants(SizeVariantBuilder builder) {
        final MenuBar menuBar = new MenuBar(true);
        menuBar.addItem("Small", builder.create(AbstractCreateCommand.Size.SMALL));
        menuBar.addItem("Medium", builder.create(AbstractCreateCommand.Size.MEDIUM));
        menuBar.addItem("Full Width", builder.create(AbstractCreateCommand.Size.LARGE));
        return menuBar;
    }


    private MenuBar createShapeMenuBar() {
        final MenuBar menuBar = new MenuBar(false);
        String[] names = new String[]{"arrow-down-1.png", "arrow-down-2.png", "arrow-left-1.png", "arrow-left-2.png", "arrow-right-1.png", "arrow-right-2.png", "arrow-up-1.png", "arrow-up-2.png", "circle_1.png", "circle_2.png", "circle_3.png", "star-1.png", "star-2.png", "star-3.png", "tick-1.png", "x-1.png"};
        for (String name : names) {
            final MenuBar shapeVariants = createMenuBarForShapeVariants(name);
            menuBar.addItem(new SafeHtmlBuilder().appendHtmlConstant("<img src='" + createUrlForDecoration(name, "black") + "' width='24' height='24'/>").toSafeHtml(), shapeVariants);
        }
        return menuBar;
    }

    private MenuBar createMenuBarForShapeVariants(String name) {
        final MenuBar menuBar = new MenuBar(true);
        createMenuItemForDecoration(name, menuBar, "black");
        createMenuItemForDecoration(name, menuBar, "red");
        createMenuItemForDecoration(name, menuBar, "green");
        createMenuItemForDecoration(name, menuBar, "white");
        return menuBar;
    }

    private void createMenuItemForDecoration(String name, MenuBar menuBar, String variant) {
        menuBar.addItem(new SafeHtmlBuilder().appendHtmlConstant("<img src='" + createUrlForDecoration(name, variant) + "' width='24' height='24'/>").toSafeHtml(), new CreateDecorationCommand(poolURI, LSDDictionaryTypes.BITMAP_IMAGE_2D, createUrlForDecoration(name, variant), AbstractCreateCommand.Size.DEFAULT, "default"));
    }

    private String createUrlForDecoration(String name, String theme) {
        return BrowserUtil.convertRelativeUrlToAbsolute("./_decorations/" + theme + "/" + name);
    }


}
