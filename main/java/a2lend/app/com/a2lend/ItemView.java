package a2lend.app.com.a2lend;

import android.widget.ImageView;

/**
 * Created by Igbar on 2/2/2018.
 */

public class ItemView {

   public  Item item;
   public ImageView imageView;

    public ItemView(Item item, ImageView imageView) {
        this.item = item;
        this.imageView = imageView;
    }

    public ItemView(ImageView imageView) {
        this.imageView = imageView;
    }

    public ItemView(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
