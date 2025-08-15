package mod.tjt01.lapislib.item;

import com.google.common.base.Suppliers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

public class CustomArmorMaterial implements ArmorMaterial {
    public final ResourceLocation id;
    private final EnumMap<ArmorItem.Type, Integer> durability;
    private final EnumMap<ArmorItem.Type, Integer> defense;
    private final int enchantmentValue;
    private final Supplier<SoundEvent> equipSound;
    private final Supplier<Ingredient> repairIngredient;
    private final float toughness;
    private final float knockbackResistance;

    private CustomArmorMaterial(
            ResourceLocation id, EnumMap<ArmorItem.Type, Integer> durability, EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue, Supplier<SoundEvent> equipSound, Supplier<Ingredient> repairIngredient,
            float toughness, float knockbackResistance
    ) {
        this.id = id;
        this.durability = durability;
        this.defense = defense;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.repairIngredient = repairIngredient;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    /**
     * Creates a new <code>CustomArmorMaterial.Builder</code>
     */
    public static Builder builder(ResourceLocation id) {
        return new Builder(id);
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return durability.get(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return defense.get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound.get();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return id.toString();
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public static class Builder {
        public final ResourceLocation id;
        private final EnumMap<ArmorItem.Type, Integer> durability = new EnumMap<>(ArmorItem.Type.class);
        private final EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        private int enchantmentValue = 0;
        private Supplier<SoundEvent> equipSound = Suppliers.ofInstance(SoundEvents.ARMOR_EQUIP_GENERIC);
        private Supplier<Ingredient> repairIngredient = Suppliers.ofInstance(Ingredient.EMPTY);
        private float toughness = 0.0F;
        private float knockbackResistance = 0.0F;

        private Builder(ResourceLocation id) {
            this.id = id;
            this.durability(0, 0, 0, 0).defense(0, 0, 0, 0);
        }

        /**
         * Sets the durability for each armor piece
         */
        public Builder durability(int helmet, int chestplate, int leggings, int boots) {
            this.durability.put(ArmorItem.Type.HELMET, helmet);
            this.durability.put(ArmorItem.Type.CHESTPLATE, chestplate);
            this.durability.put(ArmorItem.Type.LEGGINGS, leggings);
            this.durability.put(ArmorItem.Type.BOOTS, boots);
            return this;
        }

        /**
         * Sets the durability for an armor piece
         */
        public Builder durability(ArmorItem.Type type, int durability) {
            this.durability.put(type, durability);
            return this;
        }

        /**
         * Sets the durability for all armor pieces based on a multiplier.
         * @see ArmorMaterials
         */
        public Builder durability(int multiplier) {
            return this.durability(11 * multiplier, 16 * multiplier, 15 * multiplier, 13 * multiplier);
        }

        /**
         * Sets the defense for each armor piece
         */
        public Builder defense(int helmet, int chestplate, int leggings, int boots) {
            this.defense.put(ArmorItem.Type.HELMET, helmet);
            this.defense.put(ArmorItem.Type.CHESTPLATE, chestplate);
            this.defense.put(ArmorItem.Type.LEGGINGS, leggings);
            this.defense.put(ArmorItem.Type.BOOTS, boots);
            return this;
        }

        /**
         * Sets the defense for an armor piece
         */
        public Builder defense(ArmorItem.Type type, int defense) {
            this.defense.put(ArmorItem.Type.HELMET, defense);
            return this;
        }

        /**
         * Sets the enchantment value for the armor material
         */
        public Builder enchantmentValue(int value) {
            this.enchantmentValue = value;
            return this;
        }

        /**
         * Sets the equip sound for the armor material
         */
        public Builder equipSound(Supplier<SoundEvent> sound) {
            this.equipSound = Suppliers.memoize(sound::get);
            return this;
        }

        /**
         * Sets the equip sound to the default sound event
         */
        public Builder equipSound() {
            this.equipSound = Suppliers.ofInstance(SoundEvents.ARMOR_EQUIP_GENERIC);
            return this;
        }

        /**
         * Sets the repair ingredient for the armor material
         */
        public Builder repairIngredient(Supplier<Ingredient> ingredient) {
            this.repairIngredient = Suppliers.memoize(repairIngredient::get);
            return this;
        }

        /**
         * Sets the repair ingredient for the armor material to an item ingredient
         */
        public Builder repairItem(Supplier<Item> ingredient) {
            this.repairIngredient = Suppliers.memoize(() -> Ingredient.of(ingredient.get()));
            return this;
        }

        /**
         * Sets the repair ingredient for the armor material to an item tag
         */
        public Builder repairTag(TagKey<Item> tagKey) {
            this.repairIngredient = Suppliers.memoize(() -> Ingredient.of(tagKey));
            return this;
        }

        /**
         * Sets the toughness for the armor material.
         */
        public Builder toughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        /**
         * Sets the knockback resistance for the armor material; as a percentage.
         */
        public Builder knockbackResistance(float resistance) {
            this.knockbackResistance = resistance;
            return this;
        }

        /**
         * @return a new <code>CustomArmorMaterial</code>
         */
        public CustomArmorMaterial build() {
            return new CustomArmorMaterial(
                    this.id, this.durability, this.defense, this.enchantmentValue, this.equipSound, this.repairIngredient, this.toughness, this.knockbackResistance
            );
        }
    }
}
