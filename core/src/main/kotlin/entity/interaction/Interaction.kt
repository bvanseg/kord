package dev.kord.core.entity.interaction

import dev.kord.common.Locale
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.interaction.InteractionBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of [ActionInteraction](https://discord.com/developers/docs/interactions/slash-commands#interaction).
 *
 * @see ActionInteraction
 * @see DataInteraction
 */
public sealed interface Interaction : InteractionBehavior {
    public val data: InteractionData

    override val id: Snowflake get() = data.id

    override val applicationId: Snowflake
        get() = data.applicationId

    /**
     * The channel id where the interaction took place.
     */
    override val channelId: Snowflake get() = data.channelId

    /**
     * a continuation token for responding to the interaction
     */
    override val token: String get() = data.token

    /**
     * The type of the interaction.
     */
    public val type: InteractionType get() = data.type

    public val user: UserBehavior

    /**
     * The selected language of the invoking user
     *
     * This is available on all interaction types except [InteractionType.Ping]
     */
    public val locale: Locale? get() = data.locale.value

    /**
     * The guild's preferred locale, if invoked in a guild.
     */
    public val guildLocale: Locale? get() = data.guildLocale.value

    /**
     * read-only property, always 1
     */
    public val version: Int get() = data.version

    abstract override fun withStrategy(strategy: EntitySupplyStrategy<*>): Interaction

    public companion object {
        public fun from(
            data: InteractionData,
            kord: Kord,
            strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): Interaction {
            return when {
                data.type == InteractionType.Component -> ComponentInteraction(data, kord, strategy.supply(kord))
                data.type == InteractionType.AutoComplete -> AutoCompleteInteraction(data, kord, strategy.supply(kord))
                data.guildId !is OptionalSnowflake.Missing -> GuildApplicationCommandInteraction(
                    data,
                    kord,
                    strategy.supply(kord)
                )
                else -> GlobalApplicationCommandInteraction(data, kord, strategy.supply(kord))
            }
        }
    }
}
