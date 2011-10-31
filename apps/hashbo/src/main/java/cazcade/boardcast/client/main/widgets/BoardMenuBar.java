package cazcade.boardcast.client.main.widgets;

import cazcade.boardcast.client.main.menus.board.*;
import cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog;
import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidPermissionChangeType;
import cazcade.liquid.api.LiquidPermissionScope;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * @author neilellis@cazcade.com
 */
public class BoardMenuBar extends MenuBar {

    private LiquidURI poolURI;
    private MenuBar addMenubar;
    private MenuBar backgroundMenuBar;
    private MenuBar accessMenuBar;


    public interface SizeVariantBuilder<T extends CreateItemCommand> {

        T create(CreateItemCommand.Size size);

    }

    public BoardMenuBar() {
        super(false);
        setVisible(true);
        setAnimationEnabled(false);
        setAutoOpen(true);
        setFocusOnHoverEnabled(true);
    }


    public void init(LSDEntity board, boolean modifierOptions, final ChangeBackgroundDialog backgroundDialog) {
        this.poolURI = board.getURI();
        clearItems();
        if (modifierOptions) {
            addMenubar = new MenuBar(true);
            createAddMenu(poolURI);
            final MenuItem add = addItem("Add", addMenubar);
            add.addStyleName("board-menu-add");
        }
        if (board.getBooleanAttribute(LSDAttribute.EDITABLE) && backgroundDialog != null) {
            backgroundMenuBar = new MenuBar(true);
            addItem("Background", backgroundMenuBar);
            backgroundMenuBar.addItem("Change", new Command() {
                @Override
                public void execute() {
                    backgroundDialog.center();
                    backgroundDialog.show();
                }
            });
        }
        if (board.getBooleanAttribute(LSDAttribute.ADMINISTERABLE)) {
            accessMenuBar = new MenuBar(true);
            addItem("Access", accessMenuBar);
            createAccessMenu(board);
        }

    }

    private void createAccessMenu(LSDEntity board) {
        if (board.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW)) {
            if (board.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
                accessMenuBar.addItem("Make Readonly", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC_READONLY, poolURI));
            } else {
                accessMenuBar.addItem("Make Writeable", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC, poolURI));

            }
            accessMenuBar.addItem("Make Private", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PRIVATE, poolURI));
        } else {
            accessMenuBar.addItem("Make Public (Readonly)", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC_READONLY, poolURI));
            accessMenuBar.addItem("Make Public (Writeable)", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC, poolURI));
        }


    }

    private void createAddMenu(final LiquidURI poolURI) {
//        addItem(new SafeHtmlBuilder().appendHtmlConstant("<img alt=\"add\" src=\"_images/add.png\"/>").toSafeHtml(), addMenu);

        addMenubar.addItem("Sticky", new CreateRichTextCommand(poolURI, LSDDictionaryTypes.STICKY, AbstractCreateCommand.Size.DEFAULT, "default"));

        addMenubar.addItem("Plain Text", createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Override
            public CreateItemCommand create(CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "default");
            }
        }));

        addMenubar.addItem("White Text", createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Override
            public CreateItemCommand create(CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "white");
            }
        }));


        addMenubar.addItem("Black Text", createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Override
            public CreateItemCommand create(CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "black");
            }
        }));

        addMenubar.addItem("Caption", new CreateRichTextCommand(poolURI, LSDDictionaryTypes.CAPTION, AbstractCreateCommand.Size.DEFAULT, "default"));

        addMenubar.addItem("Photograph",
                createMenuBarForSizeVariants(new SizeVariantBuilder() {
                    @Override
                    public CreateItemCommand create(CreateItemCommand.Size size) {
                        return new CreatePhotoCommand(poolURI, LSDDictionaryTypes.PHOTO2D, size, "default");
                    }
                }));

        addMenubar.addItem("Webpage Link",
                createMenuBarForSizeVariants(new SizeVariantBuilder() {
                    @Override
                    public CreateItemCommand create(CreateItemCommand.Size size) {
                        return new CreateWebsiteCommand(poolURI, LSDDictionaryTypes.WEBPAGE, size, "default");
                    }
                }));

        addMenubar.addItem("YouTube Video",
                createMenuBarForSizeVariants(new SizeVariantBuilder() {
                    @Override
                    public CreateItemCommand create(CreateItemCommand.Size size) {
                        return new CreateYouTubeCommand(poolURI, LSDDictionaryTypes.YOUTUBE_MOVIE, size, "default");
                    }
                }));

        addMenubar.addItem("Decorations", createShapeMenuBar());

        if (ClientApplicationConfiguration.isAlphaFeatures()) {
            addMenubar.addItem("Your Card", new CreateAliasRefCommand(poolURI, LSDDictionaryTypes.ALIAS_REF, UserUtil.getCurrentAlias().getURI()));
//                                subMenu.addItem("Custom Object (ALPHA)", new CreateCustomObjectCommand(poolURI, LSDDictionaryTypes.CUSTOM_OBJECT));
            addMenubar.addItem("Checklist (ALPHA)", new CreateChecklistCommand(poolURI, LSDDictionaryTypes.CHECKLIST_POOL));
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
