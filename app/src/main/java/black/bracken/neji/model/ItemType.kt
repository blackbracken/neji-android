package black.bracken.neji.model

import black.bracken.neji.firebase.document.ItemTypeEntity

data class ItemType(
    val name: String
) {

    override fun toString() = name

}

fun ItemType(entity: ItemTypeEntity): ItemType {
    return ItemType(name = entity.name)
}
