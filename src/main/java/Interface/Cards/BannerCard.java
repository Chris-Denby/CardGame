package Interface.Cards;

import Interface.Constants;

import javax.swing.*;
import java.awt.*;

public class BannerCard extends Card
{

    private Constants.BannerType artifactType;
    private int durability = 5;
    private JLabel durabilityLabel = new JLabel(durability+"");

    public BannerCard(String cardName, int imageId) {
        super(cardName, imageId);

        cardNameLabel.setText("Equipment");
        GridLayout gridLayout = new GridLayout(0,3,0,0);
        bodyBox.setLayout(gridLayout);
        bodyBox.setBackground(new Color(0,0,0,180));
        bodyBox.remove(textBox);
        bodyBox.add(durabilityLabel);
    }

    public void setArtifactType(Constants.BannerType type)
    {
        artifactType = type;
        abilityLabel.setText(artifactType.name());
    }

    public Constants.BannerType getArtifactType()
    {
        return artifactType;
    }

    public void decrementDurability()
    {
        durability--;
        durabilityLabel.setText(durability+"");
    }




}
