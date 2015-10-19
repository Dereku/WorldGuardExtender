/**
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */
package wgextender.utils;

import wgextender.WGExtender;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.permission.ActorSelectorLimits;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.ExtendingCuboidRegionSelector;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.storage.ChunkStore;
import com.sk89q.worldedit.IncompleteRegionException;

public class WEUtils {

    public static boolean expandVert(org.bukkit.entity.Player player) {
        Player weplayer = WGExtender.getInstance().getWorldEdit().wrapPlayer(player);
        LocalSession session = WGExtender.getInstance().getWorldEdit().getSession(player);
        try {
            Region region = session.getSelection(weplayer.getWorld());
            region.expand(
                    new Vector(0, (weplayer.getWorld().getMaxY() + 1), 0),
                    new Vector(0, -(weplayer.getWorld().getMaxY() + 1), 0)
            );
            session.getRegionSelector(weplayer.getWorld()).learnChanges();
            session.getRegionSelector(weplayer.getWorld()).explainRegionAdjust(weplayer, session);
            return true;
        } catch (IncompleteRegionException | RegionOperationException e) {
        }
        return false;
    }

    public static boolean chunkSelect(org.bukkit.entity.Player player) {
        Player weplayer = WGExtender.getInstance().getWorldEdit().wrapPlayer(player);
        LocalSession session = WGExtender.getInstance().getWorldEdit().getSession(player);
        World world = weplayer.getWorld();
        try {
            Region region = session.getSelection(weplayer.getWorld());
            Vector2D min2D = ChunkStore.toChunk(region.getMinimumPoint());
            Vector2D max2D = ChunkStore.toChunk(region.getMaximumPoint());
            Vector min = new Vector(min2D.getBlockX() * 16, 0, min2D.getBlockZ() * 16);
            Vector max = new Vector(max2D.getBlockX() * 16 + 15, world.getMaxY(), max2D.getBlockZ() * 16 + 15);
            CuboidRegionSelector selector;
            if (session.getRegionSelector(world) instanceof ExtendingCuboidRegionSelector) {
                selector = new ExtendingCuboidRegionSelector(world);
            } else {
                selector = new CuboidRegionSelector(world);
            }
            selector.selectPrimary(min, ActorSelectorLimits.forActor(weplayer));
            selector.selectSecondary(max, ActorSelectorLimits.forActor(weplayer));
            session.setRegionSelector(world, selector);
            return true;
        } catch (IncompleteRegionException ex) {
        }
        return false;
    }
}
