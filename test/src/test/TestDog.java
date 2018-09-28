package test;

//Parent class
class Animal {
    String type;
    public Animal(){
       this.type= "Animal";
   }

}

//Child class
class Dog extends Animal {

   String type;
   public Dog(){
       this.type ="Dog";
   }

}

//Main Class To Test
class TestDog{

	public static void main( String [] args)

	{    Animal a = new Animal();   
      Dog b = new Dog();        
      Animal c = new Dog();     

      a = b;
      System.out.println(a.type);
      System.out.println(b.type);
      System.out.println(c.type);

	}
}