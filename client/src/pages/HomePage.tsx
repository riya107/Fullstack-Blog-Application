import React, { useEffect, useState } from "react"; 
import {
  Card,
  CardHeader,
  CardBody,
  Tabs,
  Tab,
  Chip,
  Button,
} from "@nextui-org/react";
import { motion } from "framer-motion";
import { apiService, Post, Category, Tag } from "../services/apiService";

// Helper: strips HTML tags from content
const stripHtml = (html: string): string => {
  const doc = new DOMParser().parseFromString(html, "text/html");
  return doc.body.textContent || "";
};

const HomePage: React.FC = () => {
  const [posts, setPosts] = useState<Post[] | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [tags, setTags] = useState<Tag[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedCategory, setSelectedCategory] = useState<string | undefined>(undefined);
  const [selectedTag, setSelectedTag] = useState<string | undefined>(undefined);

  const cardColors = [
    "from-indigo-500/20 to-indigo-200/40",
    "from-pink-500/20 to-pink-200/40",
    "from-green-500/20 to-green-200/40",
    "from-yellow-500/20 to-yellow-200/40",
    "from-purple-500/20 to-purple-200/40",
    "from-blue-500/20 to-blue-200/40",
  ];

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [postsResponse, categoriesResponse, tagsResponse] = await Promise.all([
          apiService.getPosts({
            categoryId: selectedCategory, // Filter by category
            tagId: selectedTag,
          }),
          apiService.getCategories(),
          apiService.getTags(),
        ]);
        setPosts(postsResponse);
        setCategories(categoriesResponse);
        setTags(tagsResponse);
        setError(null);
      } catch (err) {
          setError("Error loading content. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [selectedCategory, selectedTag]);

  return (
    <div className="max-w-7xl mx-auto px-4 space-y-10">
      {/* Hero Section */}
      <section className="text-center py-12 bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500 text-white rounded-2xl shadow-lg">
        <h1 className="text-4xl font-extrabold">Welcome to my blog ✨</h1>
        <p className="mt-3 text-lg opacity-90">
          Discover exciting posts about tech, lifestyle and more.
        </p>
      </section>

      {/* Categories Tabs */}
      <Card>
        <CardHeader>
          <h2 className="text-2xl font-bold">Categories</h2>
        </CardHeader>
        <CardBody>
          <Tabs
            selectedKey={selectedCategory || "all"}
            onSelectionChange={(key) =>
              setSelectedCategory(key === "all" ? undefined : (key as string))
            }
            variant="underlined"
            classNames={{
              tabList: "gap-6",
              cursor: "w-full bg-primary",
            }}
          >
            <Tab key="all" title="All posts" />
            {categories.map((cat) => (
              <Tab key={cat.id} title={`${cat.name} (${cat.postCount})`} />
            ))}
          </Tabs>
        </CardBody>
      </Card>

      {/* Tags */}
      {tags.length > 0 && (
        <div className="flex gap-2 flex-wrap">
          {tags.map((tag) => (
            <Chip
              key={tag.id}
              onClick={() =>
                setSelectedTag(selectedTag === tag.id ? undefined : tag.id)
              }
              color={selectedTag === tag.id ? "primary" : "default"}
              variant={selectedTag === tag.id ? "solid" : "bordered"}
              className="cursor-pointer"
            >
              #{tag.name}
            </Chip>
          ))}
        </div>
      )}

      {/* Posts Grid */}
      {error && (
        <div className="p-4 text-red-500 bg-red-50 rounded-lg">{error}</div>
      )}

      {loading ? (
        <p className="text-center text-default-500">Loading posts...</p>
      ) : posts && posts.length > 0 ? (
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {posts.map((post, index) => (
            <motion.div
              key={post.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
            >
              <Card
                className={`h-full shadow-md hover:shadow-xl transition-shadow rounded-2xl bg-gradient-to-br ${
                  cardColors[index % cardColors.length]
                }`}
              >
                <CardHeader className="flex flex-col items-start">
                  <h3 className="text-xl font-semibold">{post.title}</h3>
                  <p className="text-sm text-default-500">
                    {post.category?.name}
                  </p>
                </CardHeader>
                <CardBody className="flex flex-col">
                  <p className="line-clamp-3 text-default-700 mb-4">
                    {stripHtml(post.content).slice(0, 150)}...
                  </p>
                  <Button
                    as="a"
                    href={`/posts/${post.id}`}
                    color="primary"
                    variant="flat"
                    className="mt-auto"
                  >
                    Read more →
                  </Button>
                </CardBody>
              </Card>
            </motion.div>
          ))}
        </div>
      ) : (
        <p className="text-center text-default-500">
          No posts available yet.
        </p>
      )}
    </div>
  );
};

export default HomePage;
