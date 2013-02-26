/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.pool.objects;

import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.vortex.gwt.util.client.VortexThreadSafeExecutor;
import cazcade.vortex.pool.api.PoolObjectPresenterContainer;
import cazcade.vortex.pool.api.PoolPresenter;
import cazcade.vortex.pool.objects.alias.AliasReferencePresenter;
import cazcade.vortex.pool.objects.alias.AliasReferenceView;
import cazcade.vortex.pool.objects.checklist.ChecklistPresenter;
import cazcade.vortex.pool.objects.checklist.ChecklistView;
import cazcade.vortex.pool.objects.image.ImagePresenter;
import cazcade.vortex.pool.objects.image.ImageView;
import cazcade.vortex.pool.objects.microblog.MicroBlogPresenter;
import cazcade.vortex.pool.objects.microblog.MicroBlogView;
import cazcade.vortex.pool.objects.photo.PhotoPresenter;
import cazcade.vortex.pool.objects.photo.PhotoView;
import cazcade.vortex.pool.objects.richtext.CaptionView;
import cazcade.vortex.pool.objects.richtext.NoteView;
import cazcade.vortex.pool.objects.richtext.RichTextPresenter;
import cazcade.vortex.pool.objects.richtext.StickyView;
import cazcade.vortex.pool.objects.website.WebsitePresenter;
import cazcade.vortex.pool.objects.website.WebsiteView;
import cazcade.vortex.pool.objects.youtube.YouTubePresenter;
import cazcade.vortex.pool.objects.youtube.YouTubeView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class PoolObjectPresenterFactory {

    @Nullable
    public static PoolObjectPresenter getPresenterForEntity(final PoolPresenter poolPresenter, @Nonnull final TransferEntity entity, final VortexThreadSafeExecutor threadSafeExecutor) {
        if (entity.canBe(Types.T_PHOTO2D)) {
            return new PhotoPresenter(poolPresenter, entity, new PhotoView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_WEBPAGE)) {
            return new WebsitePresenter(poolPresenter, entity, new WebsiteView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_BITMAP_IMAGE_2D)) {
            return new ImagePresenter(poolPresenter, entity, new ImageView(), threadSafeExecutor);
            //        } else if (entity.canBe(Types.CUSTOM_OBJECT)) {
            //            return new CustomObjectPresenter(poolPresenter, entity, new CustomObjectView(), features.getCustomObjectEditor(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_ALIAS_REF)) {
            return new AliasReferencePresenter(poolPresenter, entity, new AliasReferenceView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_YOUTUBE_MOVIE)) {
            return new YouTubePresenter(poolPresenter, entity, new YouTubeView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_NOTE)) {
            return new RichTextPresenter(poolPresenter, entity, new NoteView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_STICKY)) {
            return new RichTextPresenter(poolPresenter, entity, new StickyView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_CAPTION)) {
            return new RichTextPresenter(poolPresenter, entity, new CaptionView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_CHECKLIST_POOL)) {
            return new ChecklistPresenter(poolPresenter, entity, new ChecklistView(), threadSafeExecutor);
        } else if (entity.canBe(Types.T_MICROBLOG)) {
            return new MicroBlogPresenter(poolPresenter, entity, new MicroBlogView(), threadSafeExecutor);
        } else {
            return null;
        }
    }

    @Nullable
    public static PoolObjectPresenter getPresenterForEntity(@Nonnull final PoolObjectPresenterContainer poolObjectPresenterContainer, @Nonnull final TransferEntity entity, final VortexThreadSafeExecutor threadSafeExecutor) {
        if (poolObjectPresenterContainer.getType().canBe(Types.T_POOL2D)) {
            return getPresenterForEntity((PoolPresenter) poolObjectPresenterContainer, entity, threadSafeExecutor);
        } else {
            return null;
        }
    }
}
