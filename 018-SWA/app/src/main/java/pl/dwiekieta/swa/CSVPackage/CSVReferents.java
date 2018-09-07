package pl.dwiekieta.swa.CSVPackage;

public class CSVReferents {
    private CSVNode[] Node;
    private String[] Id;

    private int NumberOfReferences;
    private int Size;
    private static int MinimumSize = 8;

    /*
    ****** MEMORY MANAGEMENT *************
     */
    private void resize(){
        if(Size == 0){
            Node = new CSVNode[MinimumSize];
            Id = new String[MinimumSize];

            Size = MinimumSize;
            return;
        }

        double fillRange = (double) NumberOfReferences/Size;

        if((fillRange >= 0.4) && (fillRange <= 0.85))
            return;

        if((fillRange < 0.4) && (NumberOfReferences < 8))
            return;

        int newSize;
        CSVNode[] newNode;
        String[] newId;

        if(fillRange < 0.4)
            newSize = Size/2;
        else
            newSize = Size*2;

        newNode = new CSVNode[newSize];
        newId = new String[newSize];

        for(int i = 0; i < Size; ++i){
            newNode[i] = Node[i];
            newId[i] = Id[i];
        }

        Node = newNode;
        Id = newId;

        Size = newSize;
    }

    /*
    ***** INDEX FINDING **********
     */
    private int findIndexById(String id){
        if(NumberOfReferences == 0)
            return -1;

        for(int i = 0; i < NumberOfReferences; ++i){
            if(Id[i].equals(id))
                return i;
        }

        return -2;
    }

    /*
    ****** CONSTRUCTORS **********
     */
    private void init(){
        NumberOfReferences = 0;
        Size = 0;
    }
    public CSVReferents(){
        init();
        resize();
    }

    /*
    ***** METHODS **********
     */
    public void add(CSVNode node, String id){
        Node[NumberOfReferences] = node;
        Id[NumberOfReferences] = id;

        NumberOfReferences++;
        resize();
    }

    public void clear(){
        init();
        resize();
    }

    public final CSVNode findNodeById(String id){
        int index = findIndexById(id);

        if(index < 0)
            return null;

        return Node[index];
    }

    public final CSVNode findNodeByIndex(int index){
        if((index < 0) || (index >= NumberOfReferences))
            return null;

        return Node[index];
    }

    /*
    **** GETS ***********
     */
    public final CSVNode getLastNode(){
        if(NumberOfReferences == 0)
            return null;

        return Node[NumberOfReferences - 1];
    }

    public final boolean isIdFree(String id){
        if(findIndexById(id) < 0)
            return true;
        return false;
    }

    public final int getNumberOfReferences(){
        return NumberOfReferences;
    }
}
