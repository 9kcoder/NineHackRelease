package me.ninethousand.ninehack.managers;

import me.ninethousand.ninehack.NineHack;
import me.ninethousand.ninehack.feature.Feature;
import me.ninethousand.ninehack.feature.features.client.ClientFont;
import me.ninethousand.ninehack.util.MathsUtil;
import me.ninethousand.ninehack.util.TimerUtil;
import me.ninethousand.ninehack.util.customfont.CustomFont;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class TextManager implements NineHack.Globals {
    private final TimerUtil idleTimer = new TimerUtil();
    public int scaledWidth;
    public int scaledHeight;
    public int scaleFactor;
    public CustomFont menuFont = new CustomFont(new Font("Odibee Sans", Font.PLAIN, 40), true, true);
    public CustomFont customFont = new CustomFont(new Font("Verdana", ClientFont.style.getValue().ordinal(), ClientFont.size.getValue()), true, true);
    private boolean idling;

    public TextManager() {
        this.updateResolution();
    }

    public void update() {
        this.customFont = new CustomFont(new Font("Verdana", ClientFont.style.getValue().ordinal(), ClientFont.size.getValue()), true, true);
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        this.drawString(text, x, y, color, true);
    }

    public float drawStringCustomMenu(String text, float x, float y, int color, boolean shadow) {
        if (shadow) {
            return this.menuFont.drawStringWithShadow(text, x, y, color);
        }
        return this.menuFont.drawString(text, x, y, color);
    }


    public float drawString(String text, float x, float y, int color, boolean shadow) {
        if (ClientFont.INSTANCE.isEnabled()) {
            if (shadow) {
                return this.customFont.drawStringWithShadow(text, x, y, color);
            }
            return this.customFont.drawString(text, x, y, color);
        }
        return TextManager.mc.fontRenderer.drawString(text, x, y, color, shadow);
    }

    public void drawRainbowString(String text, float x, float y, int startColor, float factor, boolean shadow) {
        Color currentColor = new Color(startColor);
        float hueIncrement = 1.0f / factor;
        String[] rainbowStrings = text.split("\u00a7.");
        float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[0];
        float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[1];
        float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[2];
        int currentWidth = 0;
        boolean shouldRainbow = true;
        boolean shouldContinue = false;
        for (int i = 0; i < text.length(); ++i) {
            char currentChar = text.charAt(i);
            char nextChar = text.charAt(MathsUtil.clamp(i + 1, 0, text.length() - 1));
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                shouldRainbow = false;
            } else if ((String.valueOf(currentChar) + nextChar).equals("\u00a7+")) {
                shouldRainbow = true;
            }
            if (shouldContinue) {
                shouldContinue = false;
                continue;
            }
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                String escapeString = text.substring(i);
                this.drawString(escapeString, x + (float) currentWidth, y, Color.WHITE.getRGB(), shadow);
                break;
            }
            this.drawString(String.valueOf(currentChar).equals("\u00a7") ? "" : String.valueOf(currentChar), x + (float) currentWidth, y, shouldRainbow ? currentColor.getRGB() : Color.WHITE.getRGB(), shadow);
            if (String.valueOf(currentChar).equals("\u00a7")) {
                shouldContinue = true;
            }
            currentWidth += this.getStringWidth(String.valueOf(currentChar));
            if (String.valueOf(currentChar).equals(" ")) continue;
            currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
            currentHue += hueIncrement;
        }
    }

    public int getStringWidth(String text) {
        if (ClientFont.INSTANCE.isEnabled()) {
            return this.customFont.getStringWidth(text);
        }
        return TextManager.mc.fontRenderer.getStringWidth(text);
    }

    public int getStringWidthMenu(String text) {
        return this.menuFont.getStringWidth(text);
    }

    public int getFontHeight() {
        if (ClientFont.INSTANCE.isEnabled()) {
            String text = "A";
            return this.customFont.getStringHeight(text);
        }
        return TextManager.mc.fontRenderer.FONT_HEIGHT;
    }

    public int getFontHeightMenu() {
        String text = "A";
        return this.customFont.getStringHeight(text);
    }

    public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
        this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
    }

    public Font getCurrentFont() {
        return this.customFont.getFont();
    }

    public void updateResolution() {
        this.scaledWidth = TextManager.mc.displayWidth;
        this.scaledHeight = TextManager.mc.displayHeight;
        this.scaleFactor = 1;
        boolean flag = mc.isUnicode();
        int i = TextManager.mc.gameSettings.guiScale;
        if (i == 0) {
            i = 1000;
        }
        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }
        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }
        double scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        double scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    public String getIdleSign() {
        if (this.idleTimer.passedMs(500L)) {
            this.idling = !this.idling;
            this.idleTimer.reset();
        }
        if (this.idling) {
            return "_";
        }
        return "";
    }
}


