public class ObjectLabel {
    private String Label;
    private double Confidence;
    public ObjectLabel(String imgFood, double conVal) {
        Label = imgFood;
        Confidence = conVal;

    }

    public void setLabel(String food) {
        this.Label = food;
    }

    public void setConfidence(double cV) {
        this.Confidence = cV;
    }

    public String getLabel() {
        return Label;
    }

    public double getConfidence() {
        return Confidence;
    }

    @Override
    public String toString() {
        return String.format("ObjectLabel{label= %s, confidence= %.2f}" , Label , Confidence);
    }


    if
            ((inventory[i].getClass()).equals(YerbaMate.class))
}
