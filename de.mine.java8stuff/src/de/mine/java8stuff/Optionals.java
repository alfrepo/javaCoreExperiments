package de.mine.java8stuff;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class Optionals {

	public static void main(String[] args) {
		Creature creature = new Creature("Сапожник козоед");
		System.out.println(creature.getName().get()); // pretty sure there is a name - use get()
		
		// add defaults
		System.out.print("Optional empty: ");
		System.out.println(creature.getSkill().orElse(Skill.Unskilled));
		
		System.out.print("String empty: ");
		System.out.println("");
		
		System.out.println(creature.getEmptyName().orElse("Unpresent Name"));
		
		if(creature.getEmptyName().isPresent()){
			System.out.println("Name is empty");
		}
		
		// Nested Optional - map maps the value INSIDE optional. Nested Optional may be the result
		Optional<Optional<Power>> optWeapon = creature.weapon.map(Weapon::getPower);
		
		// One Level Optional - flatMap reduces nested Option
		creature.weapon = Optional.of(new Weapon());
		Optional<Power> optWeaponFlat = creature.weapon
				.flatMap(Weapon::getPower);
		
		// can call getPower on EMPTY weapon
		creature.weapon = Optional.empty();
		Optional<Power> optWeaponFlatEmpty = creature.weapon
				.flatMap(Weapon::getPower);
		
		// iterating Weapon -> Power -> Element without NPE and without nested Optionals
		Optional<Element> optElementFlatEmpty = creature.weapon
				.flatMap(Weapon::getPower)
				.flatMap(Power::getElement);
		
		// primitives
		OptionalInt oInt = parseInt("123");
		OptionalLong.of(Long.parseLong("123"));
		
		System.out.println("Ready");
	}
	
	// turns a String into an INT, producing an empty optional in case of an Exception 
	public static OptionalInt parseInt(String string){
		try {
			return OptionalInt.of(Integer.parseInt(string));
		} catch (NumberFormatException e) {
			return OptionalInt.empty();
		}
	}

	enum Skill{Magic, Fighting, Comedy, Unskilled}
	
	static class Creature{
		Optional<String> name;
		Optional<Skill> skill;
		Optional<Weapon> weapon = Optional.of(new Weapon());
		
		Creature(String name){
			this(name, null);
		}
		Creature(String name, Skill skill){
			this.name = Optional.ofNullable(name);
			this.skill = Optional.ofNullable(skill);
		}
		
		
		Optional<String> getName(){
			return name;
		}
		Optional<String> getEmptyName(){
			// explicitly say that this var is nullable
			return Optional.empty();			// equivalent to value==null
//			return Optional.ofNullable(null); 	// ok but too long
//			return Optional.of(null); 			// would produce a NullPointerException
		}
		Optional<Skill> getSkill() {
			return skill;
		}
	}

	static class Weapon{
		String name = "Sword";
		Optional<Power> power = Optional.of(new Power());
		
		public Optional<Power> getPower() {
			return power;
		}
	}
	
	static class Power{
		String name = "Power";
		Optional<Element> element = Optional.of(Element.Fire);
		
		public Optional<Element> getElement() {
			return element;
		}
	}
	
	enum Element{ Fire, Water, Earth, Air}


}
