/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.widgets;

import cazcade.boardcast.client.main.menus.board.*;
import cazcade.boardcast.client.main.widgets.board.ChangeBackgroundDialog;
import cazcade.boardcast.client.main.widgets.board.PublicBoard;
import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidPermissionChangeType;
import cazcade.liquid.api.LiquidPermissionScope;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.vortex.common.client.UserUtil;
import cazcade.vortex.dnd.client.browser.BrowserUtil;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import cazcade.vortex.gwt.util.client.ClientLog;
import cazcade.vortex.gwt.util.client.analytics.Track;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class BoardMenuBar extends MenuBar {
    public interface SizeVariantBuilder<T extends CreateItemCommand> {
        @Nonnull T create(CreateItemCommand.Size size);
    }

    @Nullable
    private LiquidURI   poolURI;
    private MenuBar     accessMenuBar;
    private PublicBoard boardWidget;

    public BoardMenuBar() {
        super(false);
        setAnimationEnabled(false);
        setAutoOpen(true);
        setFocusOnHoverEnabled(true);
    }

    public void init(@Nonnull PublicBoard boardWidget, @Nonnull final LSDBaseEntity board, final boolean modifierOptions, @Nullable final ChangeBackgroundDialog backgroundDialog) {
        this.boardWidget = boardWidget;
        poolURI = board.getURI();
        clearItems();
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(final Throwable reason) {
                ClientLog.log(reason);
            }

            @Override
            public void onSuccess() {
                if (modifierOptions) {
                    createAddMenu(poolURI, backgroundDialog, board, BoardMenuBar.this);
                }
                addSeparator();
                if (board.getBooleanAttribute(LSDAttribute.ADMINISTERABLE)) {
                    accessMenuBar = new MenuBar(true);
                    addItem(iconWithName("key", "Access"), accessMenuBar);
                    createAccessMenu(board);
                }
                if (!UserUtil.isAnonymousOrLoggedOut() && ClientApplicationConfiguration.isAlphaFeatures()) {
                    createCollaborateMenu(board);
                }
            }
        });
    }

    private SafeHtml iconWithName(String name, String title) {
        return SafeHtmlUtils.fromTrustedString("<img class='menubar-icon' width='56px' height='56px' src='/_images/icons/"
                                               + name
                                               + ".png'/><div class='menubar-icon-title'>"
                                               + (!title.isEmpty() ? title : "&nbsp;")
                                               + "</div>");
    }

    private void createAddMenu(final LiquidURI poolURI, @Nullable final ChangeBackgroundDialog backgroundDialog, @Nonnull final LSDBaseEntity board, final MenuBar menubar) {
        if (board.getBooleanAttribute(LSDAttribute.EDITABLE) && backgroundDialog != null) {
            menubar.addItem(iconWithName("background", "Backdrop"), new Command() {
                @Override
                public void execute() {
                    backgroundDialog.show();
                    Track.getInstance().trackEvent("Background", "Dialog shown");
                }
            });
        }

        menubar.addItem(iconWithName("sticky", "Sticky"), new CreateRichTextCommand(poolURI, LSDDictionaryTypes.STICKY, AbstractCreateCommand.Size.DEFAULT, "default"));

        menubar.addItem(iconWithName("caption", "Caption"), new CreateRichTextCommand(poolURI, LSDDictionaryTypes.CAPTION, AbstractCreateCommand.Size.DEFAULT, "default"));

        menubar.addItem(iconWithName("plain", "Text"), createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Nonnull @Override
            public CreateItemCommand create(final CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "default");
            }
        }, false));

        menubar.addItem(iconWithName("lined", "Lined"), createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Nonnull @Override
            public CreateItemCommand create(final CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "lined");
            }
        }, false));


        menubar.addItem(iconWithName("white_on_black", "Card"), createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Nonnull @Override
            public CreateItemCommand create(final CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "black");
            }
        }, false));

        menubar.addItem(iconWithName("black_on_white", "Card"), createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Nonnull @Override
            public CreateItemCommand create(final CreateItemCommand.Size size) {
                return new CreateRichTextCommand(poolURI, LSDDictionaryTypes.NOTE, size, "white");
            }
        }, false));


        menubar.addItem(iconWithName("photos", "Photo"), createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Nonnull @Override
            public CreateItemCommand create(final CreateItemCommand.Size size) {
                return new CreatePhotoCommand(poolURI, LSDDictionaryTypes.PHOTO2D, size, "default");
            }
        }, true));

        menubar.addItem(iconWithName("world", "Webpage"), createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Nonnull @Override
            public CreateItemCommand create(final CreateItemCommand.Size size) {
                return new CreateWebsiteCommand(poolURI, LSDDictionaryTypes.WEBPAGE, size, "default");
            }
        }, true));

        menubar.addItem(iconWithName("movie", "YouTube"), createMenuBarForSizeVariants(new SizeVariantBuilder() {
            @Nonnull @Override
            public CreateItemCommand create(final CreateItemCommand.Size size) {
                return new CreateYouTubeCommand(poolURI, LSDDictionaryTypes.YOUTUBE_MOVIE, size, "default");
            }
        }, false));

        menubar.addItem(iconWithName("squiggle", "Marks"), createShapeMenuBar());

        if (ClientApplicationConfiguration.isAlphaFeatures()) {
            menubar.addItem(iconWithName("address", ""), new CreateAliasRefCommand(poolURI, LSDDictionaryTypes.ALIAS_REF, UserUtil.getCurrentAlias()
                                                                                                                                  .getURI()));
            //                                subMenu.addItem("Custom Object (ALPHA)", new CreateCustomObjectCommand(poolURI, LSDDictionaryTypes.CUSTOM_OBJECT));
            menubar.addItem(iconWithName("list", ""), new CreateChecklistCommand(poolURI, LSDDictionaryTypes.CHECKLIST_POOL));
        }
    }

    @Nonnull
    private MenuBar createMenuBarForSizeVariants(@Nonnull final SizeVariantBuilder builder, final boolean includeVerySmall) {
        final MenuBar menuBar = new MenuBar(true);
        if (includeVerySmall) {
            menuBar.addItem("Thumb", builder.create(AbstractCreateCommand.Size.THUMBNAIL));
        }
        menuBar.addItem("Small", builder.create(AbstractCreateCommand.Size.SMALL));
        menuBar.addItem("Medium", builder.create(AbstractCreateCommand.Size.MEDIUM));
        menuBar.addItem("Full", builder.create(AbstractCreateCommand.Size.LARGE));
        return menuBar;
    }

    @Nonnull
    private MenuBar createShapeMenuBar() {
        final MenuBar menuBar = new MenuBar(false);
        final String[] names = new String[]{"arrow-down-1.png", "arrow-down-2.png", "arrow-left-1.png", "arrow-left-2.png",
                "arrow-right-1.png", "arrow-right-2.png", "arrow-up-1.png", "arrow-up-2.png", "circle_1.png", "circle_2.png",
                "circle_3.png", "star-1.png", "star-2.png", "star-3.png", "tick-1.png", "x-1.png"};
        for (final String name : names) {
            final MenuBar shapeVariants = createMenuBarForShapeVariants(name);
            menuBar.addItem(new SafeHtmlBuilder().appendHtmlConstant("<img src='" +
                                                                     createUrlForDecoration(name, "black") +
                                                                     "' width='24' height='24'/>").toSafeHtml(), shapeVariants);
        }
        return menuBar;
    }

    @Nonnull
    private MenuBar createMenuBarForShapeVariants(final String name) {
        final MenuBar menuBar = new MenuBar(true);
        createMenuItemForDecoration(name, menuBar, "black");
        createMenuItemForDecoration(name, menuBar, "red");
        createMenuItemForDecoration(name, menuBar, "green");
        createMenuItemForDecoration(name, menuBar, "white");
        return menuBar;
    }

    private void createMenuItemForDecoration(final String name, @Nonnull final MenuBar menuBar, final String variant) {
        assert poolURI != null;
        menuBar.addItem(new SafeHtmlBuilder().appendHtmlConstant("<img src='" +
                                                                 createUrlForDecoration(name, variant) +
                                                                 "' width='24' height='24'/>")
                                             .toSafeHtml(), new CreateDecorationCommand(poolURI, LSDDictionaryTypes.BITMAP_IMAGE_2D, createUrlForDecoration(name, variant), AbstractCreateCommand.Size.DEFAULT, "default"));
    }

    private String createUrlForDecoration(final String name, final String theme) {
        return BrowserUtil.convertRelativeUrlToAbsolute("./_decorations/" + theme + "/" + name);
    }

    private void createAccessMenu(@Nonnull final LSDBaseEntity board) {
        if (board.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.VIEW)) {
            if (board.hasPermission(LiquidPermissionScope.WORLD, LiquidPermission.MODIFY)) {
                accessMenuBar.addItem("Readonly", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC_READONLY, poolURI));
            } else {
                accessMenuBar.addItem("Editable", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC, poolURI));
            }
            accessMenuBar.addItem("Hide", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PRIVATE, poolURI));
        } else {
            accessMenuBar.addItem("Publish (Readonly)", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC_READONLY, poolURI));
            accessMenuBar.addItem("Publish (Editable)", new ChangePermissionCommand(LiquidPermissionChangeType.MAKE_PUBLIC, poolURI));
        }
    }

    private void createCollaborateMenu(@Nonnull final LSDBaseEntity board) {
        MenuBar collaborateMenuBar = new MenuBar(true);
        addItem(iconWithName("collaborate", "Collab"), collaborateMenuBar);
        final MenuItem chat = new MenuItem(iconWithName("chat", "Chat"), (Command) null);

        chat.setCommand(new Command() {
            @Override
            public void execute() {
                boardWidget.toggleChat();
                if ("Chat".equals(chat.getText())) {
                    chat.setText("End Chat");
                } else {
                    chat.setText("Chat");
                }
                Track.getInstance().trackEvent("Chat", "Switched to Chat");
            }
        });

        collaborateMenuBar.addItem(chat);
    }
}
