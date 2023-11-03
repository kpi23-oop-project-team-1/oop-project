const path = require("path");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const TerserPlugin = require('terser-webpack-plugin');

let htmlPageNames = ['signin', 'signup'];
let multipleHtmlPlugins = htmlPageNames.map(name => {
  return new HtmlWebpackPlugin({
    template: `./public/${name}.html`, 
    filename: `${name}.html`,
    chunks: [`${name}`]
  })
});

module.exports = {
  entry: { 
    main: "./src/main.tsx",
    signin: "./src/signin.tsx",
    signup: "./src/signup.tsx"
  },
  target: ["web", "es5"],
  mode: "development",
  output: {
    path: path.resolve(__dirname, "build"),
    filename: "[name].js",
    clean: true
  },
  resolve: {
    extensions: [".js", ".ts", ".tsx"],
  },
  devtool: 'source-map',
  devServer: {
    static: {
      directory: path.join(__dirname, 'public'),
    },
    compress: true,
    port: 9000,
  },
  module: {
    rules: [
      {
        test: /\.(ts|tsx)$/,
        exclude: "/node_modules/",
        loader: "ts-loader"
      },
      {
        test: /\.scss$/,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader',
          'postcss-loader',
          'sass-loader',
        
        ],
      },
      {
        test: /\.(png|jpe?g|gif)$/i,
        use: [
          {
            loader: 'url-loader',
          },
        ],
      },
      {
        test: /\.svg$/,
        use: [
          {
            loader: "babel-loader"
          },
          {
            loader: "react-svg-loader",
            options: {
              jsx: true 
            }
          }
        ]
      }
    ],
  },
  plugins: [

    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, "public", "index.html"),
      filename: `index.html`,
      chunks: ['main']
    }),
    new MiniCssExtractPlugin({
      filename: "[name].css",
      chunkFilename: "[id].css",
    }),
  ].concat(multipleHtmlPlugins),
  optimization: {
    minimizer: [
      new CssMinimizerPlugin(),
      new TerserPlugin({
        parallel: true,
      }),
    ],
  },
};