import Data.Complex
import Control.Monad
import Control.Arrow

main = do
        c <- map read . words <$> getLine :: IO [Double]
        n <- readLn
        z <- readLn :: IO Double
        i <- readLn :: IO Integer
        grid (map (:+ 0) c) n z i

dif r = poly $ zipWith (*) (init r) $ fromIntegral <$> iterate pred (length r - 1)

poly [] _ = 0
poly [r] _ = r
poly (r:rs:rss) x = poly ((r * x + rs):rss) x

solve _ _ _ 0 = (0, 0)
solve x eq der it = let dx = eq x / der x
                    in if (realPart . abs) dx < 1e-15
                      then (x, it)
                      else solve (x - dx) eq der $ pred it

grid r n z it = forM_ gr $ \i -> do
                  forM_ gr $ \j -> do
                    putStr . show . rnd $ solve (j :+ i) (poly r) (dif r) it
                    putStr " "
                  putStrLn []
             where gr = (/z) <$> [(-n)..n]
                   comp = do f <- show . realPart; g <- show . imagPart; return $ f ++ ":+" ++ g
                   rnd = first $ comp . fmap ((/ 1e10) . fromIntegral . round . (* 1e10))
