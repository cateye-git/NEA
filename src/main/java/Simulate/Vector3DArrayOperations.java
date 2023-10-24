package Simulate;

public class Vector3DArrayOperations {
    public static Vector3D[] addVectors(Vector3D[] array1, Vector3D[] array2){
        Vector3D[] out = new Vector3D[array1.length];
        if(array2.length == array1.length){
            for(int index = 0;index<array1.length;index++){
                out[index] = Vector3D.add(array1[index],array2[index]);
            }
        }
        else{
            throw new RuntimeException("cannot add arrays of different sizes");
        }
        return out;
    }

    public static Vector3D[] multiplyVectors(Vector3D[] array, double mult){
        Vector3D[] out = new Vector3D[array.length];
        for(int index = 0;index<array.length;index++){
            out[index] = array[index].multiply(mult);
        }
        return out;
    }
}
